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
        Notification("Marco", "Ciao sei una merda umana"),
        Notification("Luca", "Ciao sei una merda umana"),
        Notification("Bagno", "Tocca a Marco", Icons.Default.WaterDrop),
        Notification("Luca", "Ciao sei una merda umana"),
        Notification("Luca", "Ciao sei una merda umana"),
        Notification("Luca", "Ciao sei una merda umana"),
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

data class Notification(val Nome: String, val UltimoMessaggio: String, val Icon: ImageVector = Icons.Default.WaterDrop ){

}

