package cohappy.frontend.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.model.SingleChatViewModel
import cohappy.frontend.model.SingleChatViewModelFactory
import cohappy.frontend.view.chat.SingleChatView

@Composable
fun SingleChatScreen(
    chatCode: String,
    userToken: String?,
    viewModel: SingleChatViewModel = viewModel(factory = SingleChatViewModelFactory()),
    onBackClick: () -> Unit,
    onNavigateToAnnuncio: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val mioUserCode = userToken?.replace("\"", "")?.trim() ?: ""

    LaunchedEffect(chatCode) {
        viewModel.initChat(chatCode, mioUserCode)
    }

    SingleChatView(
        uiState = uiState,
        onSendClick = { testo -> viewModel.sendMessage(testo) },
        onBackClick = onBackClick,
        onHeaderClick = {
            if (uiState.resolvedAnnuncioId.isNotBlank()) {
                onNavigateToAnnuncio(uiState.resolvedAnnuncioId)
            }
        },
        onPhotoClick = { TODO() }
    )
}