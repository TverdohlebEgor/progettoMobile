package cohappy.frontend.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.RemoveUserDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                    profileImageBytes = data.images?.firstOrNull()
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
            } catch (e: Exception) {
            }
        }
    }
}