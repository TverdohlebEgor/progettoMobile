package cohappy.frontend.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import cohappy.frontend.repository.AdListRepository

class AdListViewModel : ViewModel() {
    private val repository = AdListRepository()

    var adsList by mutableStateOf<List<GetHouseAdvertesimentDTO>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var searchQuery by mutableStateOf("")
        private set

    fun loadAdvertisements() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = withContext(Dispatchers.IO) { repository.fetchAds() }
                if (response.isSuccessful && response.body() != null) {
                    adsList = response.body()!!
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun updateSearchQuery(newQuery: String) {
        searchQuery = newQuery
    }

    fun getFilteredAds(): List<GetHouseAdvertesimentDTO> {
        return adsList.filter { annuncio ->
            (annuncio.street?.contains(searchQuery, ignoreCase = true) == true) ||
                    (annuncio.region?.contains(searchQuery, ignoreCase = true) == true)
        }
    }
}