package cohappy.frontend.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.client.dto.enum.HouseStateEnum
import cohappy.frontend.client.dto.request.CreateHouseAdvertisementDTO
import cohappy.frontend.client.dto.request.ModifyHouseAdvertisementDTO
import cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO
import cohappy.frontend.repository.CreateAdRepository
import kotlinx.coroutines.launch

class CreateAdViewModel(private val repository: CreateAdRepository = CreateAdRepository()) : ViewModel() {

    var price by mutableStateOf("")
        private set
    var description by mutableStateOf("")
        private set
    var selectedImages by mutableStateOf<List<ByteArray>>(emptyList())
        private set

    var isEditMode by mutableStateOf(false)
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

    fun fetchExistingAd(houseCode: String) {
        isLoading = true
        viewModelScope.launch {
            try {
                val response = repository.getAdvertisement(houseCode)
                if (response.isSuccessful && response.body() != null) {
                    val ad = response.body()!!
                    price = ad.costPerMonth?.toString() ?: ""
                    description = ad.description ?: ""
                    selectedImages = ad.images ?: emptyList()
                    isEditMode = true
                }
            } catch (e: Exception) {
                Log.e("CreateAdVM", "Errore nel recupero annuncio", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun publishOrUpdateAdvertisement(houseCode: String, userToken: String) {
        if (isLoading) return

        val priceDouble = price.replace(",", ".").toDoubleOrNull()

        if (priceDouble == null || description.isBlank()) {
            errorMessage = "Inserisci un prezzo valido e una descrizione"
            return
        }

        isLoading = true
        errorMessage = null
        isSuccess = false

        viewModelScope.launch {
            try {
                val tokenPulito = userToken.replace("\"", "").trim()

                val response = if (isEditMode) {
                    val dto = ModifyHouseAdvertisementDTO(
                        houseCode = houseCode,
                        state = HouseStateEnum.PUBLIC,
                        description = description,
                        images = selectedImages.ifEmpty { null },
                        costPerMonth = priceDouble.toInt()
                    )
                    repository.modifyAdvertisement(dto)
                } else {
                    val dto = CreateHouseAdvertisementDTO(
                        houseCode = houseCode,
                        images = selectedImages.ifEmpty { null },
                        state = HouseStateEnum.PUBLIC,
                        publishedBy = tokenPulito,
                        description = description,
                        costPerMonth = priceDouble.toInt()
                    )
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
