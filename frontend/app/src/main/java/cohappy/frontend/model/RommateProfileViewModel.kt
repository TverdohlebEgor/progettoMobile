package cohappy.frontend.model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.AddAdminDTO
import cohappy.frontend.client.dto.request.PatchUserDTO
import cohappy.frontend.client.dto.request.ModifyHouseDTO
import cohappy.frontend.client.dto.request.RemoveUserDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class RoommateItem(
    val userCode: String,
    val name: String,
    val surname: String,
    val isAdmin: Boolean,
    val isMe: Boolean
)

class HouseProfileViewModel : ViewModel() {

    var userName by mutableStateOf("Caricamento...")
        private set
    var userSurname by mutableStateOf("")
        private set
    var profileImageBytes by mutableStateOf<ByteArray?>(null)
        private set
    var isLoading by mutableStateOf(true)
        private set
    var hasLeftHouse by mutableStateOf(false)
        private set

    var showRoommatesPopup by mutableStateOf(false)
        private set
    var roommatesList by mutableStateOf<List<RoommateItem>>(emptyList())
        private set
    var isCurrentUserAdmin by mutableStateOf(false)
        private set
    var isRoommatesLoading by mutableStateOf(false)
        private set

    var isUpdatingCode by mutableStateOf(false)
        private set
    var codeUpdateError by mutableStateOf<String?>(null)
        private set

    var houseAddress by mutableStateOf("Caricamento...")
        private set
    var isUpdatingAddress by mutableStateOf(false)
        private set
    var addressUpdateError by mutableStateOf<String?>(null)
        private set

    fun loadProfile(userToken: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val tokenPulito = userToken.replace("\"", "").trim()
                val response = withContext(Dispatchers.IO) {
                    ClientSingleton.userApi.getUserProfile(tokenPulito)
                }
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    userName = data.name ?: "Utente"
                    userSurname = data.surname ?: ""
                    val imageByteArray = data.images?.firstOrNull()
                    if (imageByteArray != null && imageByteArray.isNotEmpty()) {
                        profileImageBytes = imageByteArray
                    }
                } else {
                    userName = "Errore API"
                }
            } catch (e: Exception) {
                userName = "Offline"
            } finally {
                isLoading = false
            }
        }
    }

    fun loadHouseDetails(houseCode: String) {
        viewModelScope.launch {
            if (houseCode.isNotBlank()) {
                try {
                    val response = withContext(Dispatchers.IO) {
                        ClientSingleton.houseApi.getHouse(houseCode)
                    }
                    if (response.isSuccessful && response.body() != null) {
                        val house = response.body()!!
                        houseAddress = "${house.street ?: "Via Sconosciuta"} ${house.civicNumber ?: ""}".trim()
                    } else {
                        houseAddress = "Via Roma, 10"
                    }
                } catch (e: Exception) {
                    Log.e("HouseProfileVM", "Errore caricamento casa", e)
                    houseAddress = "Via Roma, 10"
                }
            } else {
                houseAddress = "Via Roma, 10"
            }
        }
    }

    fun uploadNewImage(userToken: String, imageBytes: ByteArray) {
        viewModelScope.launch {
            try {
                profileImageBytes = imageBytes
                val tokenPulito = userToken.replace("\"", "").trim()

                val patchRequest = PatchUserDTO(
                    userCode = tokenPulito,
                    images = listOf(imageBytes)
                )

                val response = withContext(Dispatchers.IO) {
                    ClientSingleton.userApi.patchUser(patchRequest)
                }

                if (response.isSuccessful) {
                    Log.d("HouseProfileVM", "✅ Immagine salvata sul DB!")
                } else {
                    Log.e("HouseProfileVM", "❌ Backend ha rifiutato: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HouseProfileVM", "🚨 Errore di rete upload foto", e)
            }
        }
    }

    fun leaveHouse(userToken: String, houseCode: String) {
        viewModelScope.launch {
            try {
                val tokenPulito = userToken.replace("\"", "").trim()
                val dto = RemoveUserDTO(houseCode = houseCode, userCode = tokenPulito)
                val response = withContext(Dispatchers.IO) {
                    ClientSingleton.houseApi.removeUser(dto)
                }
                if (response.isSuccessful) {
                    hasLeftHouse = true
                }
            } catch (e: Exception) {}
        }
    }

    fun openRoommatesPopup(houseCode: String, userToken: String) {
        showRoommatesPopup = true
        loadRoommates(houseCode, userToken)
    }

    fun closeRoommatesPopup() {
        showRoommatesPopup = false
    }

    fun loadRoommates(houseCode: String, userToken: String) {
        if (houseCode.isBlank()) return

        viewModelScope.launch {
            isRoommatesLoading = true
            try {
                val tokenPulito = userToken.replace("\"", "").trim()

                val houseResponse = withContext(Dispatchers.IO) {
                    ClientSingleton.houseApi.getHouse(houseCode)
                }

                if (houseResponse.isSuccessful && houseResponse.body() != null) {
                    val house = houseResponse.body()!!
                    val admins = house.admins ?: emptyList()
                    val regularUsers = house.users ?: emptyList()

                    val allUserCodes = (admins + regularUsers).distinct()
                    val items = mutableListOf<RoommateItem>()

                    isCurrentUserAdmin = admins.contains(tokenPulito)

                    for (uCode in allUserCodes) {
                        try {
                            val userResponse = withContext(Dispatchers.IO) {
                                ClientSingleton.userApi.getUserProfile(uCode)
                            }
                            if (userResponse.isSuccessful && userResponse.body() != null) {
                                val uData = userResponse.body()!!
                                items.add(
                                    RoommateItem(
                                        userCode = uCode,
                                        name = uData.name ?: "Utente",
                                        surname = uData.surname ?: "",
                                        isAdmin = admins.contains(uCode),
                                        isMe = uCode == tokenPulito
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            Log.e("HouseProfileVM", "Errore caricamento profilo coinquilino $uCode", e)
                        }
                    }
                    roommatesList = items.sortedByDescending { it.isAdmin }
                }
            } catch (e: Exception) {
                Log.e("HouseProfileVM", "Errore caricamento coinquilini", e)
            } finally {
                isRoommatesLoading = false
            }
        }
    }

    fun promoteToAdmin(houseCode: String, targetUserCode: String, userToken: String) {
        viewModelScope.launch {
            try {
                val dto = AddAdminDTO(houseCode = houseCode, userCode = targetUserCode)
                val response = withContext(Dispatchers.IO) { ClientSingleton.houseApi.addAdmin(dto) }

                if (response.isSuccessful) {
                    Log.d("HouseProfileVM", "✅ Promosso ad Admin sul DB!")
                    roommatesList = roommatesList.map {
                        if (it.userCode == targetUserCode) it.copy(isAdmin = true) else it
                    }.sortedByDescending { it.isAdmin }
                } else {
                    Log.e("HouseProfileVM", "❌ Egor ha rifiutato la promozione: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HouseProfileVM", "🚨 Errore di rete", e)
            }
        }
    }

    fun kickUser(houseCode: String, targetUserCode: String, userToken: String) {
        viewModelScope.launch {
            try {
                val dto = RemoveUserDTO(houseCode = houseCode, userCode = targetUserCode)
                val response = withContext(Dispatchers.IO) { ClientSingleton.houseApi.removeUser(dto) }

                if (response.isSuccessful) {
                    roommatesList = roommatesList.filter { it.userCode != targetUserCode }
                } else {
                    Log.e("HouseProfileVM", "❌ Egor ha rifiutato il kick: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("HouseProfileVM", "🚨 Errore di rete kick", e)
            }
        }
    }

    fun updateHouseCode(oldHouseCode: String, newHouseCode: String) {
        viewModelScope.launch {
            isUpdatingCode = true
            codeUpdateError = null
            try {
                val oldCodePulito = oldHouseCode.replace("\"", "").trim()
                val newCodePulito = newHouseCode.replace("\"", "").trim()

                if (newCodePulito.isBlank()) {
                    codeUpdateError = "Il codice non può essere vuoto"
                    return@launch
                }

                val dto = ModifyHouseDTO(
                    houseCode = oldCodePulito,
                    newHouseCode = newCodePulito
                )

                val response = withContext(Dispatchers.IO) { ClientSingleton.houseApi.modifyHouse(dto) }

                if (response.isSuccessful) {
                    Log.d("HouseProfileVM", "✅ Codice casa aggiornato con successo!")
                } else {
                    val errorBody = response.errorBody()?.string() ?: ""
                    Log.e("HouseProfileVM", "❌ Errore 400/409 dal server: $errorBody")
                    
                    if (response.code() == 409 || errorBody.contains("exists", ignoreCase = true)) {
                        codeUpdateError = "Codice già esistente!"
                    } else {
                        codeUpdateError = "Errore backend (${response.code()})"
                    }
                }
            } catch (e: Exception) {
                Log.e("HouseProfileVM", "🚨 Errore di rete update codice", e)
                codeUpdateError = "Errore di connessione"
            } finally {
                isUpdatingCode = false
            }
        }
    }

    fun updateHouseAddress(houseCode: String, newAddress: String) {
        viewModelScope.launch {
            isUpdatingAddress = true
            addressUpdateError = null
            try {
                val dto = ModifyHouseDTO(
                    houseCode = houseCode,
                    images = null,
                    costPerMonth = null,
                    country = null,
                    region = null,
                    street = newAddress,
                    civicNumber = null
                )

                val response = withContext(Dispatchers.IO) { ClientSingleton.houseApi.modifyHouse(dto) }

                if (response.isSuccessful) {
                    Log.d("HouseProfileVM", "✅ Indirizzo aggiornato con successo!")
                    houseAddress = newAddress
                } else {
                    addressUpdateError = "Errore backend: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("HouseProfileVM", "🚨 Errore di rete update indirizzo", e)
                addressUpdateError = "Errore di connessione"
            } finally {
                isUpdatingAddress = false
            }
        }
    }
}