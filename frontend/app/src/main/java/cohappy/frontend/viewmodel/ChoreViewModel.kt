package cohappy.frontend.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.PatchChoreDTO
import cohappy.frontend.model.Chore
import cohappy.frontend.repository.ChoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChoreViewModel : ViewModel() {
    private val repository = ChoreRepository()

    var isLoading by mutableStateOf(false)
        private set

    var nomeUtente by mutableStateOf("Caricamento...")
        private set

    var chores by mutableStateOf<List<Chore>>(emptyList())
        private set

    fun loadUserData(userToken: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val cleanToken = userToken.replace("\"", "").trim()

                if (cleanToken.isBlank()) {
                    nomeUtente = "Ospite"
                    isLoading = false
                    return@launch
                }

                val response = ClientSingleton.userApi.getUserProfile(cleanToken)

                if (response.isSuccessful && response.body() != null) {
                    val userData = response.body()!!
                    nomeUtente = userData.name ?: "Utente"

                    /**
                     * funziona con modifica API
                     * */

                    val houseCode = userData.houseCode
                    if (!houseCode.isNullOrBlank()) {
                        val choresResponse = repository.fetchUserChores(houseCode)

                        if (choresResponse.isSuccessful && choresResponse.body() != null) {
                            val rawChores = choresResponse.body()!!
                            val mappedChores = mutableListOf<Chore>()

                            for (dto in rawChores) {
                                val assigneeName = dto.assignedToName ?: if (dto.assignedTo == cleanToken) "Te" else "Coinquilino"
                                mappedChores.add(
                                    Chore(
                                        choreCode = dto.choreCode ?: "",
                                        title = dto.name ?: "",
                                        description = dto.description ?: "",
                                        assignedToCode = dto.assignedTo ?: "",
                                        assigneeName = assigneeName,
                                        isCompleted = dto.completed,
                                        dayLabel = "Task"
                                    )
                                )
                            }
                            chores = mappedChores
                        } else {
                            chores = emptyList()
                        }
                    } else {
                        chores = emptyList()
                    }
                } else {
                    nomeUtente = "Errore API"
                }
            } catch (e: Exception) {
                nomeUtente = "Offline"
            } finally {
                isLoading = false
            }
        }
    }

    fun toggleChoreCompletion(choreCode: String, userCode: String, newStatus: Boolean) {
        viewModelScope.launch {
            isLoading = true
            try {
                val patchData = PatchChoreDTO(
                    choreCode = choreCode,
                    assignedTo = userCode,
                    completed = newStatus,
                    day = null,
                    houseCode = null,
                    name = null,
                    description = null
                )

                val response = repository.updateChoreStatus(patchData)

                if (response.isSuccessful) {
                    chores = chores.map { faccenda ->
                        if (faccenda.choreCode == choreCode) faccenda.copy(isCompleted = newStatus)
                        else faccenda
                    }
                }
            } catch (e: Exception) {
            } finally {
                isLoading = false
            }
        }
    }
}