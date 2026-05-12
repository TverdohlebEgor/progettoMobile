package cohappy.frontend.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO
import cohappy.frontend.repository.AdListRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AdListViewModel(
    private val repository: AdListRepository = AdListRepository()
) : ViewModel() {

    var adsList by mutableStateOf<List<GetHouseAdvertesimentDTO>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var searchQuery by mutableStateOf("")
        private set

    fun loadAdvertisements() {
        viewModelScope.launch {
            isLoading = true
            var attempt = 0
            val maxRetries = 2
            var success = false

            while (attempt <= maxRetries && !success) {
                val result = try {
                    repository.fetchAds()
                } catch (e: Exception) {
                    Result.failure(e)
                }

                if (result.isSuccess) {
                    adsList = result.getOrNull() ?: emptyList()
                    success = true
                } else {
                    attempt++
                    if (attempt <= maxRetries) {
                        delay(1000)
                    }
                }
            }
            isLoading = false
        }
    }

    fun updateSearchQuery(newQuery: String) {
        searchQuery = newQuery
    }

    fun getFilteredAds(): List<GetHouseAdvertesimentDTO> {
        if (searchQuery.isBlank()) return adsList

        return adsList.filter { annuncio ->
            val streetMatch = annuncio.street?.contains(searchQuery, ignoreCase = true) ?: false
            val regionMatch = annuncio.region?.contains(searchQuery, ignoreCase = true) ?: false
            val descMatch = annuncio.description?.contains(searchQuery, ignoreCase = true) ?: false
            streetMatch || regionMatch || descMatch
        }
    }
}
