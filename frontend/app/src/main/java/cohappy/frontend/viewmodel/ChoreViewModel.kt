package cohappy.frontend.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.client.dto.request.CreateChoreDTO
import cohappy.frontend.client.dto.request.PatchChoreDTO
import cohappy.frontend.model.Chore
import cohappy.frontend.repository.ChoreRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class ChoreViewModel(
    private val repository: ChoreRepository = ChoreRepository()
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var nomeUtente by mutableStateOf("Caricamento...")
        private set

    var currentUserCode by mutableStateOf<String?>(null)
        private set

    var houseCode by mutableStateOf<String?>(null)
        private set

    var chores by mutableStateOf<List<Chore>>(emptyList())
        private set

    var roommates by mutableStateOf<List<Pair<String, String>>>(emptyList())
        private set

    var selectedDate by mutableStateOf(LocalDate.now())
        private set

    var daysWithChores by mutableStateOf<List<LocalDate>>(emptyList())
        private set

    private var loadUserJob: Job? = null

    fun onDateSelected(date: LocalDate, userToken: String) {
        val oldDate = selectedDate
        selectedDate = date
        
        val hCode = houseCode
        if (hCode != null) {
            viewModelScope.launch {
                refreshChoresInternal(hCode)
                if (oldDate.month != date.month || oldDate.year != date.year) {
                    loadDaysWithChores(hCode)
                }
            }
        } else {
            loadUserData(userToken)
        }
    }

    fun loadUserData(userToken: String) {
        loadUserJob?.cancel()
        isLoading = true
        loadUserJob = viewModelScope.launch {
            try {
                val cleanToken = userToken.replace("\"", "").trim()
                if (cleanToken.isBlank()) {
                    nomeUtente = "Ospite"
                    chores = emptyList()
                    return@launch
                }

                val profileResp = repository.getUserProfile(cleanToken)
                if (profileResp.isSuccessful) {
                    val userData = profileResp.body()
                    if (userData != null) {
                        nomeUtente = userData.name ?: "Utente"
                        currentUserCode = userData.userCode
                        houseCode = userData.houseCode
                        
                        val hCode = houseCode
                        if (!hCode.isNullOrBlank()) {
                            // Caricamenti in parallelo
                            launch { loadHouseRoommates(hCode) }
                            launch { loadDaysWithChores(hCode) }
                            refreshChoresInternal(hCode)
                        } else {
                            chores = emptyList()
                            roommates = emptyList()
                        }
                    } else {
                        nomeUtente = "Errore Dati"
                        chores = emptyList()
                    }
                } else {
                    nomeUtente = "Errore API"
                    chores = emptyList()
                }
            } catch (e: Exception) {
                nomeUtente = "Offline"
                chores = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun refreshChoresInternal(hCode: String) {
        try {
            val choresResp = repository.fetchChores(hCode, selectedDate)
            if (choresResp.isSuccessful) {
                chores = choresResp.body()?.map { dto ->
                    val assigneeCode = dto.assignedTo
                    Chore(
                        choreCode = dto.choreCode ?: "",
                        title = dto.name ?: "",
                        description = dto.description ?: "",
                        assignedToCode = assigneeCode,
                        assigneeName = when {
                            assigneeCode == null -> "Nessuno"
                            assigneeCode == currentUserCode -> "Te"
                            else -> dto.assignedToName ?: roommates.find { it.first == assigneeCode }?.second ?: "Coinquilino"
                        },
                        isCompleted = dto.completed,
                        dayLabel = if (selectedDate == LocalDate.now()) "Oggi" else selectedDate.toString()
                    )
                } ?: emptyList()
            }
        } catch (e: Exception) {
            // In caso di errore non svuotiamo la lista per permettere l'uso offline/cache
        }
    }

    private suspend fun loadHouseRoommates(hCode: String) = coroutineScope {
        try {
            val houseResp = repository.getHouseDetails(hCode)
            if (houseResp.isSuccessful) {
                val houseData = houseResp.body()
                val allUserCodes = ((houseData?.admins ?: emptyList()) + (houseData?.users ?: emptyList())).distinct()
                
                val list = allUserCodes.map { uCode ->
                    async {
                        val resp = repository.getUserProfile(uCode)
                        if (resp.isSuccessful) uCode to (resp.body()?.name ?: "Coinquilino") else null
                    }
                }.awaitAll().filterNotNull()
                roommates = list
            }
        } catch (e: Exception) {}
    }

    fun toggleChoreCompletion(choreCode: String, userCode: String?, newStatus: Boolean) {
        val previousChores = chores
        val finalAssignedTo = userCode ?: currentUserCode

        // Aggiornamento ottimistico
        chores = chores.map {
            if (it.choreCode == choreCode) it.copy(
                isCompleted = newStatus,
                assignedToCode = finalAssignedTo,
                assigneeName = when {
                    finalAssignedTo == currentUserCode -> "Te"
                    finalAssignedTo == null -> "Nessuno"
                    else -> roommates.find { r -> r.first == finalAssignedTo }?.second ?: "Coinquilino"
                }
            ) else it
        }

        viewModelScope.launch {
            try {
                val patchData = PatchChoreDTO(
                    choreCode = choreCode,
                    assignedTo = finalAssignedTo,
                    completed = newStatus
                )
                val response = repository.updateChoreStatus(patchData)
                if (!response.isSuccessful) {
                    chores = previousChores
                } else {
                    houseCode?.let { refreshChoresInternal(it) }
                }
            } catch (e: Exception) {
                chores = previousChores
            }
        }
    }

    fun createChore(
        userToken: String,
        name: String,
        description: String,
        dates: List<LocalDate>?,
        assignedTo: String?,
        isRecursive: Boolean,
        onSuccess: () -> Unit
    ) {
        isLoading = true
        viewModelScope.launch {
            try {
                val cleanToken = userToken.replace("\"", "").trim()
                var hCode = houseCode
                var uCode = currentUserCode

                if (hCode == null || uCode == null) {
                    val profileResp = repository.getUserProfile(cleanToken)
                    if (profileResp.isSuccessful) {
                        hCode = profileResp.body()?.houseCode
                        uCode = profileResp.body()?.userCode
                        houseCode = hCode
                        currentUserCode = uCode
                    }
                }

                if (hCode != null && uCode != null) {
                    val finalDates = if (isRecursive && dates != null && dates.isNotEmpty()) {
                        val expanded = mutableListOf<LocalDate>()
                        val baseDate = dates.minOrNull() ?: LocalDate.now()
                        // Espansione deterministica: 6 mesi dalla prima data selezionata
                        val limitDate = baseDate.plusMonths(6)
                        dates.forEach { startDate ->
                            var current = startDate
                            while (current.isBefore(limitDate)) {
                                expanded.add(current)
                                current = current.plusWeeks(1)
                            }
                        }
                        expanded.sorted().distinct()
                    } else {
                        dates
                    }

                    // Se è ricorsiva o senza assegnatario specifico, mandiamo emptyMap() 
                    // affinché il backend la tratti come faccenda di casa (aperta a tutti)
                    val assignmentMap = if (!isRecursive && !finalDates.isNullOrEmpty() && assignedTo != null) {
                        mapOf(finalDates.first() to assignedTo)
                    } else {
                        emptyMap()
                    }

                    val request = CreateChoreDTO(
                        name = name,
                        description = description,
                        houseCode = hCode,
                        createdBy = uCode,
                        days = finalDates,
                        assignedTo = assignmentMap
                    )

                    val response = repository.createChore(request)
                    if (response.isSuccessful) {
                        refreshChoresInternal(hCode)
                        loadDaysWithChores(hCode)
                        onSuccess()
                    }
                }
            } catch (e: Exception) {
            } finally {
                isLoading = false
            }
        }
    }

    private var loadDaysJob: Job? = null
    private suspend fun loadDaysWithChores(hCode: String) {
        loadDaysJob?.cancel()
        loadDaysJob = viewModelScope.launch {
            try {
                val start = selectedDate.withDayOfMonth(1).minusDays(7)
                val results = coroutineScope {
                    (0..42).map { i ->
                        async {
                            val date = start.plusDays(i.toLong())
                            val resp = repository.fetchChores(hCode, date)
                            if (resp.isSuccessful && !resp.body().isNullOrEmpty()) date else null
                        }
                    }.awaitAll().filterNotNull().distinct()
                }
                daysWithChores = (daysWithChores + results).distinct()
            } catch (e: Exception) {}
        }
    }
}
