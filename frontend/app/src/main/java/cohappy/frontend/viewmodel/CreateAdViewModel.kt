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
import kotlinx.coroutines.launch

class CreateAdViewModel(
    private val repository: CreateAdRepository = CreateAdRepository()
) : ViewModel() {
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
        if (price.isBlank() || description.isBlank()) {
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
                    images = selectedImages.ifEmpty { null },
                    publishedBy = tokenPulito
                )

                val result = repository.createAdvertisement(dto)

                if (result.isSuccess) {
                    isSuccess = true
                } else {
                    errorMessage = result.exceptionOrNull()?.message ?: "Errore sconosciuto"
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