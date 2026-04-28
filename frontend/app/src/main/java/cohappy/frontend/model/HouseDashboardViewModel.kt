package cohappy.frontend.model

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.repository.HouseDashboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HouseDashboardViewModel : ViewModel() {
    private val repository = HouseDashboardRepository()

    var nomeUtente by mutableStateOf("Caricamento...")
        private set

    var profileImageBytes by mutableStateOf<ByteArray?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    val notifications = listOf(
        Notification(
            eventId = "1",
            eventType = "CHAT",
            title = "Marco",
            subtitle = "Ciao sei una merda umana",
            timestamp = "2026-04-28T18:30:00",
            imageBytes = null
        ),
        Notification(
            eventId = "2",
            eventType = "CHAT",
            title = "Luca",
            subtitle = "Ciao sei una merda umana",
            timestamp = "2026-04-28T18:32:00",
            imageBytes = null
        ),
        Notification(
            eventId = "3",
            eventType = "CHORE",
            title = "Nuova faccenda per Marco",
            subtitle = "Da completare entro il 27/04",
            timestamp = "2026-04-28T18:35:00",
            imageBytes = null
        ),
        Notification(
            eventId = "4",
            eventType = "CHAT",
            title = "Luca",
            subtitle = "Ciao sei una merda umana",
            timestamp = "2026-04-28T18:40:00",
            imageBytes = null
        ),
        Notification(
            eventId = "5",
            eventType = "CHAT",
            title = "Luca",
            subtitle = "Ciao sei una merda umana",
            timestamp = "2026-04-28T18:45:00",
            imageBytes = null
        ),
        Notification(
            eventId = "6",
            eventType = "CHAT",
            title = "Luca",
            subtitle = "Ciao sei una merda umana",
            timestamp = "2026-04-28T18:50:00",
            imageBytes = null
        )
    fun loadDashboardData(userToken: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val tokenPulito = userToken.replace("\"", "").trim()
                val response = withContext(Dispatchers.IO) { repository.fetchUserProfile(tokenPulito) }

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    nomeUtente = data.name ?: "Utente"

                    val imageByteArray = data.images?.firstOrNull()
                    if (imageByteArray != null && imageByteArray.isNotEmpty()) {
                        profileImageBytes = imageByteArray
                    }
                } else {
                    nomeUtente = "Errore API"
                }
            } catch (e: Exception) {
                Log.e("HomeGestionaleVM", "Errore caricamento dati dashboard", e)
                nomeUtente = "Offline"
            } finally {
                isLoading = false
            }
        }
    }
}

data class Notification(
    val eventId: String,
    val eventType: String,
    val title: String,
    val subtitle: String,
    val timestamp: String,
    val imageBytes: ByteArray? = null
)

