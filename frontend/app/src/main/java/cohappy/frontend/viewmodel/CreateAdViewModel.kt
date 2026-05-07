package cohappy.frontend.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.client.dto.enum.HouseStateEnum
import cohappy.frontend.client.dto.request.CreateHouseAdvertisementDTO
import cohappy.frontend.repository.CreateAdRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateAdViewModel : ViewModel() {
    private val repository = CreateAdRepository()

    var price by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var selectedImages by mutableStateOf<List<ByteArray>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var isSuccess by mutableStateOf(false)
        private set

    fun updatePrice(newPrice: String) { price = newPrice }
    fun updateDescription(newDesc: String) { description = newDesc }

    fun addImage(image: ByteArray) {
        selectedImages = selectedImages + image
    }

    fun publishAdvertisement(houseCode: String, userToken: String) {
        val priceDouble = price.replace(",", ".").toDoubleOrNull()

        if (priceDouble == null || description.isBlank()) {
            errorMessage = "Inserisci un prezzo valido e una descrizione"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val tokenPulito = userToken.replace("\"", "").trim()


                val dto = CreateHouseAdvertisementDTO(
                    houseCode = houseCode,
                    state = HouseStateEnum.PUBLIC,
                    description = description,
                    //images = selectedImages.ifEmpty { emptyList() },
                    publishedBy = tokenPulito
                )

                val response = withContext(Dispatchers.IO) {
                    repository.createAdvertisement(dto)
                }

                if (response.isSuccessful) {
                    isSuccess = true
                } else {
                    errorMessage = "Errore del server: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("CreateAdVM", "Errore di rete", e)
                errorMessage = "Nessuna connessione"
            } finally {
                isLoading = false
            }
        }
    }

    fun resetSuccess() {
        isSuccess = false
    }

    fun resetError() {
        errorMessage = null
    }
}