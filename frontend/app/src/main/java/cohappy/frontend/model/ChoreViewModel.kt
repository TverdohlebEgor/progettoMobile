package cohappy.frontend.model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.PatchChoreDTO
import cohappy.frontend.repository.ChoresRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 💅 1. LA VERA STRUTTURA DELLA FACCENDA
data class Chore(
    val choreCode: String,
    val title: String,
    val description: String,
    val assignedToCode: String, // Ci serve per capire se sei TU!
    val assigneeName: String,   // Il nome di chi deve farla ("Marco", "Sofia")
    val isCompleted: Boolean,
    val dayLabel: String
)

class ChoresViewModel : ViewModel() {
    private val repository = ChoresRepository()

    var isLoading by mutableStateOf(false)
        private set

    var nomeUtente by mutableStateOf("Caricamento...")
        private set

    var chores by mutableStateOf<List<Chore>>(emptyList())
        private set

    fun loadUserData(userToken: String) {
        viewModelScope.launch {
            try {
                val cleanToken = userToken.replace("\"", "").trim()


                chores = listOf(
                    Chore(
                        choreCode = "CHORE_001",
                        title = "Pulizia bagno",
                        description = "Sanitari, doccia e lavandino",
                        assignedToCode = cleanToken,
                        assigneeName = "Te",
                        isCompleted = false,
                        dayLabel = "Oggi"
                    ),
                    Chore(
                        choreCode = "CHORE_002",
                        title = "Pulizia cucina",
                        description = "Fornelli, piatti e spazzatura",
                        assignedToCode = "user_marco_123",
                        assigneeName = "Marco",
                        isCompleted = true,
                        dayLabel = "Martedì"
                    ),
                    Chore(
                        choreCode = "CHORE_003",
                        title = "Passare l'aspirapolvere",
                        description = "Salotto, corridoio e camere",
                        assignedToCode = "user_sofia_123", // Estraneo
                        assigneeName = "Sofia",
                        isCompleted = false,
                        dayLabel = "Giovedì"
                    )
                )

                val response = withContext(Dispatchers.IO) {
                    ClientSingleton.userApi.getUserProfile(cleanToken)
                }

                if (response.isSuccessful && response.body() != null) {
                    nomeUtente = response.body()?.name ?: "Utente"
                } else {
                    nomeUtente = "Errore API"
                }
            } catch (e: Exception) {
                Log.e("ChoresVM", "Errore caricamento dati utente", e)
                nomeUtente = "Offline"
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

                Log.d("ChoresVM", "📤 Spedisco aggiornamento: Faccenda $choreCode -> Completata: $newStatus")

                val response = withContext(Dispatchers.IO) {
                    repository.updateChoreStatus(patchData)
                }

                if (response.isSuccessful) {
                    Log.d("ChoresVM", "✅ Faccenda aggiornata con successo sul server!")

                    chores = chores.map { faccenda ->
                        if (faccenda.choreCode == choreCode) {
                            faccenda.copy(isCompleted = newStatus)
                        } else {
                            faccenda
                        }
                    }
                } else {
                    Log.e("ChoresVM", "❌ Egor ha rifiutato la patch: Errore ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e("ChoresVM", "🚨 Errore di rete o crash improvviso", e)
            } finally {
                isLoading = false
            }
        }
    }
}