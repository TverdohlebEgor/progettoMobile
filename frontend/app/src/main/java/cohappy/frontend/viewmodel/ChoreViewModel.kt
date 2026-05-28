package cohappy.frontend.viewmodel

import android.util.Log
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
                            // Avviamo il caricamento dei giorni impegnati in parallelo (non blocca le faccende)
                            launch { loadDaysWithChores(hCode) }

                            // Carichiamo prima i coinquilini, poi le faccende per avere i nomi corretti
                            loadHouseRoommates(hCode)
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
        Log.d("ChoreViewModel", "Aggiornamento lista faccende per la data: $selectedDate")
        try {
            val choresResp = repository.fetchChores(hCode, selectedDate)
            if (choresResp.isSuccessful) {
                val body = choresResp.body() ?: emptyList()
                Log.d("ChoreViewModel", "Ricevute ${body.size} faccende dal server")
                chores = body.map { dto ->
                    val assigneeCode = dto.assignedTo
                    Chore(
                        choreCode = dto.choreCode ?: "",
                        title = dto.name ?: "",
                        description = dto.description ?: "",
                        assignedToCode = assigneeCode,
                        assigneeName = when {
                            // Verifichiamo se l'occorrenza di oggi è già stata assegnata a qualcuno
                            assigneeCode.isNullOrBlank() || assigneeCode == "null" -> "Nessuno"
                            assigneeCode == currentUserCode -> "Te"
                            else -> dto.assignedToName ?: roommates.find { it.first == assigneeCode }?.second ?: "Coinquilino"
                        },
                        isCompleted = dto.completed,
                        dayLabel = if (selectedDate == LocalDate.now()) "Oggi" else selectedDate.toString()
                    )
                }
            } else {
                Log.e("ChoreViewModel", "Errore refreshChores: ${choresResp.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("ChoreViewModel", "Eccezione in refreshChoresInternal", e)
        }
    }

    private suspend fun loadHouseRoommates(hCode: String) {
        try {
            val houseResponse = repository.getHouseDetails(hCode)

            if (houseResponse.isSuccessful && houseResponse.body() != null) {
                val house = houseResponse.body()!!
                val admins = house.admins ?: emptyList()
                val regularUsers = house.users ?: emptyList()

                val allUserCodes = (admins + regularUsers).distinct()
                val items = mutableListOf<Pair<String, String>>()

                for (uCode in allUserCodes) {
                    try {
                        val userResponse = repository.getUserProfile(uCode)
                        if (userResponse.isSuccessful && userResponse.body() != null) {
                            val uData = userResponse.body()!!
                            items.add(uCode to (uData.name ?: "Utente"))
                        }
                    } catch (e: Exception) {
                        Log.e("ChoreViewModel", "Errore profilo coinquilino $uCode", e)
                    }
                }
                roommates = items
                // Aggiorniamo le faccende per mappare i nomi appena scaricati
                refreshChoresInternal(hCode)
            }
        } catch (e: Exception) {
            Log.e("ChoreViewModel", "Errore caricamento coinquilini", e)
        }
    }

    fun assignChore(choreCode: String, assigneeCode: String) {
        val hCode = houseCode ?: return
        viewModelScope.launch {
            try {
                val patchData = PatchChoreDTO(
                    choreCode = choreCode,
                    day = selectedDate,
                    assignedTo = assigneeCode,
                    houseCode = hCode
                )
                val response = repository.updateChoreStatus(patchData)
                if (response.isSuccessful) {
                    refreshChoresInternal(hCode)
                }
            } catch (e: Exception) {
                Log.e("ChoreViewModel", "Errore assegnazione faccenda", e)
            }
        }
    }

    fun toggleChoreCompletion(choreCode: String, userCode: String?, newStatus: Boolean) {
        val previousChores = chores
        // Se la faccenda viene completata e non ha un assegnatario valido, la assegniamo a chi la sta completando
        val finalAssignedTo = if (newStatus && (userCode.isNullOrBlank() || userCode == "null")) {
            currentUserCode
        } else {
            userCode ?: currentUserCode
        }

        // Aggiornamento ottimistico
        chores = chores.map {
            if (it.choreCode == choreCode) it.copy(
                isCompleted = newStatus,
                assignedToCode = finalAssignedTo,
                assigneeName = when {
                    finalAssignedTo == currentUserCode -> "Te"
                    finalAssignedTo.isNullOrBlank() || finalAssignedTo == "null" -> "Nessuno"
                    else -> roommates.find { r -> r.first == finalAssignedTo }?.second ?: "Coinquilino"
                }
            ) else it
        }

        viewModelScope.launch {
            try {
                val patchData = PatchChoreDTO(
                    choreCode = choreCode,
                    day = selectedDate, // Invia la data specifica per le ricorsive
                    assignedTo = finalAssignedTo,
                    completed = newStatus,
                    houseCode = houseCode
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
        val TAG = "ChoreViewModel"
        Log.d(TAG, "--- INIZIO PROCESSO CREAZIONE FACCENDA ---")
        Log.d(TAG, "Dati ricevuti: nome='$name', desc='$description', numero_date=${dates?.size}, assegnato_a='$assignedTo', ricorsiva=$isRecursive")

        isLoading = true
        viewModelScope.launch {
            try {
                val cleanToken = userToken.replace("\"", "").trim()
                var hCode = houseCode
                var uCode = currentUserCode

                if (hCode == null || uCode == null) {
                    Log.d(TAG, "Dati sessione mancanti. Recupero profilo in corso...")
                    val profileResp = repository.getUserProfile(cleanToken)
                    if (profileResp.isSuccessful) {
                        val body = profileResp.body()
                        hCode = body?.houseCode
                        uCode = body?.userCode
                        houseCode = hCode
                        currentUserCode = uCode
                        Log.d(TAG, "Dati recuperati con successo: houseCode='$hCode', userCode='$uCode'")
                    } else {
                        Log.e(TAG, "Impossibile recuperare il profilo: ${profileResp.errorBody()?.string()}")
                    }
                }

                if (hCode != null && uCode != null) {
                    Log.d(TAG, "Preparazione date per l'invio...")
                    val finalDates = if (isRecursive && !dates.isNullOrEmpty()) {
                        Log.d(TAG, "Espansione ricorsiva (6 mesi) avviata per ${dates.size} giorni base")
                        val expanded = mutableListOf<LocalDate>()
                        val baseDate = LocalDate.now()
                        val limitDate = baseDate.plusMonths(6)

                        dates.forEach { startDate ->
                            var current = startDate
                            // Se la data selezionata è nel passato, partiamo da oggi o dalla prossima occorrenza
                            while (current.isBefore(limitDate)) {
                                expanded.add(current)
                                current = current.plusWeeks(1)
                            }
                        }
                        val sorted = expanded.sorted().distinct()
                        Log.d(TAG, "Espansione completata: generate ${sorted.size} occorrenze")
                        sorted
                    } else {
                        Log.d(TAG, "Utilizzo date singole fornite: $dates")
                        dates
                    }

                    // Logica di assegnazione:
                    // Se l'utente ha selezionato qualcuno (assignedTo), usiamo quello per tutte le date.
                    // Se è una faccenda singola (non ricorsiva) e non ha scelto nessuno, usiamo il creatore.
                    // Se è una faccenda ricorsiva (di casa) e non ha scelto nessuno, la lasciamo "Aperta a tutti" ("null").

                    val assignmentMap = if (!finalDates.isNullOrEmpty()) {
                        val map = mutableMapOf<LocalDate, String>()
                        finalDates.forEach { date ->
                            // Come richiesto, al momento della creazione la faccenda NON viene assegnata a nessuno.
                            // Sarà assegnata in seguito o al momento del completamento.
                            map[date] = "null"
                        }
                        map
                    } else {
                        Log.w(TAG, "ATTENZIONE: Nessuna data valida generata!")
                        emptyMap()
                    }

                    Log.d(TAG, "Assegnazione configurata: ${assignmentMap.size} giorni mappati")

                    val request = CreateChoreDTO(
                        name = name,
                        description = description,
                        houseCode = hCode,
                        createdBy = uCode,
                        days = finalDates,
                        assignedTo = assignmentMap
                    )

                    Log.d(TAG, "INVIO RICHIESTA AL SERVER -> POST /api/chore/create")
                    Log.d(TAG, "Payload: $request")

                    val response = repository.createChore(request)
                    if (response.isSuccessful) {
                        Log.d(TAG, "SERVER SUCCESS: Faccenda creata correttamente")
                        refreshChoresInternal(hCode)
                        loadDaysWithChores(hCode)
                        onSuccess()
                    } else {
                        val errorMsg = response.errorBody()?.string()
                        Log.e(TAG, "SERVER ERROR (${response.code()}): $errorMsg")
                    }
                } else {
                    Log.e(TAG, "ERRORE LOGICO: houseCode o userCode sono nulli, impossibile procedere")
                }
            } catch (e: Exception) {
                Log.e(TAG, "ECCEZIONE DURANTE LA CREAZIONE:", e)
            } finally {
                isLoading = false
                Log.d(TAG, "--- FINE PROCESSO CREAZIONE ---")
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
