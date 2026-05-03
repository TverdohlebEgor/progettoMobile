package cohappy.frontend.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.CreateHouseDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateHouseViewModel : ViewModel() {

    var province by mutableStateOf("")
        private set
    var city by mutableStateOf("")
        private set
    var street by mutableStateOf("")
        private set
    var civicNumber by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var creationSuccess by mutableStateOf(false)
        private set
    var createdHouseCode by mutableStateOf("")
        private set

    fun updateProvince(it: String) { province = it }
    fun updateCity(it: String) { city = it }
    fun updateStreet(it: String) { street = it }
    fun updateCivicNumber(it: String) { civicNumber = it }

    fun createHouse(userToken: String) {
        val civic = civicNumber.toIntOrNull()

        if (province.isBlank() || city.isBlank() || street.isBlank() || civic == null) {
            errorMessage = "Compila tutti i campi correttamente"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val tokenPulito = userToken.replace("\"", "").trim()

                val dto = CreateHouseDTO(
                    country = province, // Spesso country è fisso o richiesto in un formato specifico
                    region = city,
                    street = street,
                    civicNumber = civic,
                    userCode = tokenPulito,
                    images = emptyList(), // Passiamo lista vuota invece di null
                    costPerMonth = 0      // Passiamo 0 invece di null
                )

                val response = withContext(Dispatchers.IO) {
                    ClientSingleton.houseApi.createHouse(dto)
                }

                if (response.isSuccessful) {
                    createdHouseCode = response.body() ?: ""
                    creationSuccess = true
                } else {
                    errorMessage = "Errore dal backend: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Nessuna connessione"
            } finally {
                isLoading = false
            }
        }
    }

    fun resetSuccess() {
        creationSuccess = false
    }
}