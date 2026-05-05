package cohappy.frontend.model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.client.dto.HouseStateEnum
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

    // 💅 Ora la funzione addImage c'è!
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

                // 💅 AGGIORNATO CON I PARAMETRI DEL NUOVO DTO!
                val dto = CreateHouseAdvertisementDTO(
                    houseCode = houseCode,
                    state = HouseStateEnum.PUBLIC,
                    // Togliamo costPerMonth perché Egor lo ha tolto dal DTO degli annunci!
                    description = description,
                    images = selectedImages.ifEmpty { emptyList() }, // 💅 List vuota invece di null
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