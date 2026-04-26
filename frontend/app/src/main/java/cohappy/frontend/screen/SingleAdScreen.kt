package cohappy.frontend.screen

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.view.Ad.SingleAdView
import cohappy.frontend.model.SingleAdViewModel



@Composable
fun SingleAdScreen(
    annuncioId: String,
    userToken: String?,
    onBackClick: () -> Unit,
    onChatClick: (String) -> Unit,
    onRequireLoginClick: () -> Unit,
    viewModel: SingleAdViewModel = viewModel()
) {


    LaunchedEffect(annuncioId) {
        viewModel.loadAd(annuncioId)
    }

    SingleAdView(
        adDetail = viewModel.adDetail,
        isLoading = viewModel.isLoading,
        coverBmpBytes = viewModel.coverBmpBytes,
        onBackClick = onBackClick,
        onStartChatClick = { hostCode ->
            val myUserCode = userToken?.replace("\"", "")?.trim() ?: ""

            Log.d("TAG_CHECK_CHAT", "🕵️ Controllo pre-chat. Inserzionista: '$hostCode', Io: '$myUserCode'")

            if (myUserCode.isBlank()) {
                Log.w("TAG_CHECK_CHAT", "🛑 Utente OSPITE! Lo spedisco al login.")
                onRequireLoginClick()
            } else if (hostCode.isBlank()) {
                Log.e("TAG_CHECK_CHAT", "❌ ERRORE: publishedByCode dell'annuncio è VUOTO o NULL!")
            } else if (hostCode == myUserCode) {
                Log.e("TAG_CHECK_CHAT", "❌ ERRORE: Non puoi chattare con te stesso! Questo è un tuo annuncio.")
            } else {
                Log.d("TAG_CHECK_CHAT", "✅ Check superato! Navigo verso la chat.")
                onChatClick(hostCode)
            }
        }
    )
}