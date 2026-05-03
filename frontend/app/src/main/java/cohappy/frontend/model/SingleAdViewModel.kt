package cohappy.frontend.model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO
import cohappy.frontend.repository.SingleAdRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SingleAdViewModel : ViewModel() {
    private val repository = SingleAdRepository()

    var adDetail by mutableStateOf<GetHouseAdvertesimentDTO?>(null)
        private set

    var coverBmpBytes by mutableStateOf<ByteArray?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    fun loadAd(adId: String) {
        viewModelScope.launch {
            isLoading = true
            val idPulito = adId.replace("\"", "").trim()
            var attempt = 0
            val maxRetries = 2
            var success = false

            while (attempt <= maxRetries && !success) {
                try {
                    val response = withContext(Dispatchers.IO) { repository.fetchAdDetail(idPulito) }

                    if (response.isSuccessful && response.body() != null) {
                        val data = response.body()!!
                        adDetail = data
                        Log.d("SingleAdVM", "✅ Dettaglio annuncio caricato: ${data.houseCode}")

                        val firstImage = data.images?.firstOrNull()
                        if (firstImage != null && firstImage.isNotEmpty()) {
                            coverBmpBytes = firstImage
                        }
                        success = true
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("SingleAdVM", "❌ Errore Backend (${response.code()}): $errorBody")
                        break
                    }
                } catch (e: Exception) {
                    attempt++
                    if (attempt > maxRetries) {
                        Log.e("SingleAdVM", "🚨 Errore di rete caricamento annuncio dopo $maxRetries tentativi", e)
                    } else {
                        Log.w("SingleAdVM", "⚠️ Tentativo $attempt fallito, riprovo...")
                        delay(1000)
                    }
                }
            }
            isLoading = false
        }
    }
}

//package cohappy.frontend.model
//
//import android.util.Log
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO
//import cohappy.frontend.repository.SingleAdRepository
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class SingleAdViewModel : ViewModel() {
//    private val repository = SingleAdRepository()
//
//    var adDetail by mutableStateOf<GetHouseAdvertesimentDTO?>(null)
//        private set
//
//    var coverBmpBytes by mutableStateOf<ByteArray?>(null)
//        private set
//
//    var isLoading by mutableStateOf(true)
//        private set
//
//    fun loadAd(adId: String) {
//        viewModelScope.launch {
//            isLoading = true
//            try {
//                val response = withContext(Dispatchers.IO) { repository.fetchAdDetail(adId) }
//
//                if (response.isSuccessful && response.body() != null) {
//                    val data = response.body()!!
//                    adDetail = data
//
//                    val firstImage = data.images?.firstOrNull()
//                    if (firstImage != null && firstImage.isNotEmpty()) {
//                        coverBmpBytes = firstImage
//                    }
//                } else {
//                    Log.e("SingleAdVM", "❌ Backend ha rifiutato: ${response.code()}")
//                }
//            } catch (e: Exception) {
//                Log.e("SingleAdVM", "🚨 Errore di rete caricamento annuncio", e)
//            } finally {
//                isLoading = false
//            }
//        }
//    }
//}