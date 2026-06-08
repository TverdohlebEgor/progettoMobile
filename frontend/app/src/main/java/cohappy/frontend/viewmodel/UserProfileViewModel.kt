package cohappy.frontend.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.repository.UserProfileRepository
import kotlinx.coroutines.launch

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
    var joinedHouseCode by mutableStateOf("")
        private set
    fun loadProfile(userToken: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val tokenPulito = userToken.replace("\"", "").trim()
                val result = repository.fetchUserProfile(tokenPulito)

                if (result.isSuccess) {
                    val data = result.getOrNull()!!
                    userName = data.name ?: "Utente"
                    userSurname = data.surname ?: ""

                    val imageByteArray = data.images?.firstOrNull()
                    if (imageByteArray != null && imageByteArray.isNotEmpty()) {
                        profileImageBytes = imageByteArray
                    }
                } else {
                    userName = "Errore API"
                    userSurname = ""
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

                val result = repository.updateUserImage(tokenPulito, imageBytes)

                if (result.isSuccess) {
                    Log.d("UserProfileVM", "✅ Immagine salvata sul DB con successo!")
                } else {
                    Log.e("UserProfileVM", "❌ Backend ha rifiutato l'immagine")
                }
            } catch (e: Exception) {
                Log.e("UserProfileVM", "🚨 Errore di rete upload foto", e)
            }
        }
    }

    fun joinHouse(houseCode: String, userToken: String) {
        val codicePulito = houseCode.trim()
        if (codicePulito.isBlank()) {
            houseJoinError = "Inserisci un codice valido"
            return
        }

        houseJoinError = null

        viewModelScope.launch {
            try {
                val tokenPulito = userToken.replace("\"", "").trim()
                val result = repository.joinHouse(codicePulito, tokenPulito)

                if (result.isSuccess) {
                    // Quando l'utente entra in una casa, creiamo una chat privata con ogni coinquilino
                    try {
                        val houseRes = cohappy.frontend.client.ClientSingleton.houseApi.getHouse(codicePulito)
                        if (houseRes.isSuccessful) {
                            val house = houseRes.body()
                            val roommates = (house?.admins.orEmpty() + house?.users.orEmpty()).distinct()

                            val chatRepository = cohappy.frontend.repository.ChatListRepository()

                            for (roommateCode in roommates) {
                                val cleanRoommateCode = roommateCode.replace("\"", "").trim()
                                if (cleanRoommateCode == tokenPulito) continue

                                // Recuperiamo il profilo del coinquilino per dare un nome alla chat
                                val rProfileRes = repository.fetchUserProfile(cleanRoommateCode)
                                val rProfile = rProfileRes.getOrNull()
                                val chatName = if (rProfile != null) {
                                    "${rProfile.name ?: ""} ${rProfile.surname ?: ""}".trim()
                                } else {
                                    "Chat con ${cleanRoommateCode.take(5)}"
                                }

                                chatRepository.createChat(
                                    cohappy.frontend.client.dto.request.CreateChatDTO(
                                        participating = listOf(tokenPulito, cleanRoommateCode),
                                        name = chatName.ifBlank { "Coinquilino" }
                                    )
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("UserProfileVM", "Errore creazione chat automatiche", e)
                    }

                    joinedHouseCode = codicePulito
                    navigateToHouse = true
                } else {
                    houseJoinError = result.exceptionOrNull()?.message ?: "Errore durante l'aggiunta alla casa"
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