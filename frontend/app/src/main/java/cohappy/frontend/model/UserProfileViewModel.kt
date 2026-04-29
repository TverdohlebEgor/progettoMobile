package cohappy.frontend.model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserProfileViewModel : ViewModel() {
    private val repository = UserProfileRepository()

    var userName by mutableStateOf("Caricamento...")
        private set
    var userSurname by mutableStateOf("")
        private set
    var profileImageBytes by mutableStateOf<ByteArray?>(null)
        private set
    var isLoading by mutableStateOf(true)
        private set

    var houseJoinError by mutableStateOf<String?>(null)
        private set
    var navigateToHouse by mutableStateOf(false)
        private set
    fun loadProfile(userToken: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val tokenPulito = userToken.replace("\"", "").trim()
                val response = withContext(Dispatchers.IO) { repository.fetchUserProfile(tokenPulito) }

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    userName = data.name ?: "Utente"
                    userSurname = data.surname ?: ""

                    val imageByteArray = data.images?.firstOrNull()
                    if (imageByteArray != null && imageByteArray.isNotEmpty()) {
                        profileImageBytes = imageByteArray
                    }
                } else {
                    userName = "Errore API:"
                    userSurname = "Codice ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("UserProfileVM", "Errore caricamento profilo", e)
                userName = "CRASH:"
                userSurname = "Rete o Server"
            } finally {
                isLoading = false
            }
        }
    }

    fun uploadNewImage(userToken: String, imageBytes: ByteArray) {
        viewModelScope.launch {
            try {
                profileImageBytes = imageBytes

                val tokenPulito = userToken.replace("\"", "").trim()

                val response = withContext(Dispatchers.IO) {
                    repository.updateUserImage(tokenPulito, imageBytes)
                }

                if (response.isSuccessful) {
                    Log.d("UserProfileVM", "✅ Immagine salvata sul DB con successo!")
                } else {
                    Log.e("UserProfileVM", "❌ Backend ha rifiutato l'immagine: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("UserProfileVM", "🚨 Errore di rete upload foto", e)
            }
        }
    }

    fun joinHouse(houseCode: String, userToken: String) {
        val codicePulito = houseCode.trim().uppercase()
        if (codicePulito.isBlank()) {
            houseJoinError = "Inserisci un codice valido"
            return
        }

        houseJoinError = null

        viewModelScope.launch {
            try {
                val tokenPulito = userToken.replace("\"", "").trim()
                val response = withContext(Dispatchers.IO) { repository.joinHouse(codicePulito, tokenPulito) }

                if (response.isSuccessful) {
                    navigateToHouse = true
                } else {
                    if (response.code() == 404) {
                        houseJoinError = "La casa non esiste"
                    } else if (response.code() == 409) {
                        houseJoinError = "Sei già in questa casa"
                    } else {
                        houseJoinError = "Errore dal server: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                Log.e("UserProfileVM", "Errore Join House", e)
                houseJoinError = "Nessuna connessione a internet"
            }
        }
    }

    fun resetNavigation() {
        navigateToHouse = false
    }
}