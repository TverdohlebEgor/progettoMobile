package cohappy.frontend.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO
import cohappy.frontend.repository.AdListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            var attempt = 0
            val maxRetries = 2
            var success = false

            while (attempt <= maxRetries && !success) {
                try {
                    val result = withContext(Dispatchers.IO) { repository.fetchAds() }
                    if (result.isSuccess) {
                        adsList = result.getOrNull() ?: emptyList()
                        Log.d("AdListVM", "✅ Caricati ${adsList.size} annunci")
                        success = true
                    } else {
                        Log.e("AdListVM", "❌ Errore Backend: ${result.exceptionOrNull()?.message}")
                        break
                    }
                } catch (e: Exception) {
                    attempt++
                    if (attempt > maxRetries) {
                        Log.e("AdListVM", "🚨 Errore di rete nel caricamento annunci dopo $maxRetries tentativi", e)
                    } else {
                        Log.w("AdListVM", "⚠️ Tentativo $attempt fallito, riprovo...")
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
        // Fondamentale: se non cerchi nulla, mostra tutto.
        // Impedisce che i campi null nel DB facciano sparire gli annunci.
        if (searchQuery.isBlank()) return adsList

        return adsList.filter { annuncio ->
            val streetMatch = annuncio.street?.contains(searchQuery, ignoreCase = true) ?: false
            val regionMatch = annuncio.region?.contains(searchQuery, ignoreCase = true) ?: false
            val descMatch = annuncio.description?.contains(searchQuery, ignoreCase = true) ?: false
            streetMatch || regionMatch || descMatch
        }
    }
}

//package cohappy.frontend.model
//
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import cohappy.frontend.repository.AdListRepository
//
//class AdListViewModel : ViewModel() {
//    private val repository = AdListRepository()
//
//    var adsList by mutableStateOf<List<GetHouseAdvertesimentDTO>>(emptyList())
//        private set
//
//    var isLoading by mutableStateOf(true)
//        private set
//
//    var searchQuery by mutableStateOf("")
//        private set
//
//    fun loadAdvertisements() {
//        viewModelScope.launch {
//            isLoading = true
//            try {
//                val response = withContext(Dispatchers.IO) { repository.fetchAds() }
//                if (response.isSuccessful && response.body() != null) {
//                    adsList = response.body()!!
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            } finally {
//                isLoading = false
//            }
//        }
//    }
//
//    fun updateSearchQuery(newQuery: String) {
//        searchQuery = newQuery
//    }
//
//    fun getFilteredAds(): List<GetHouseAdvertesimentDTO> {
//        return adsList.filter { annuncio ->
//            (annuncio.street?.contains(searchQuery, ignoreCase = true) == true) ||
//                    (annuncio.region?.contains(searchQuery, ignoreCase = true) == true)
//        }
//    }
//}