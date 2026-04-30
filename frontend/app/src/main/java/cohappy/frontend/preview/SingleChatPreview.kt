package cohappy.frontend.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.client.dto.response.ChatMessageDTO
import cohappy.frontend.model.ChatUiState
import cohappy.frontend.randomPhoto
import cohappy.frontend.view.chat.SingleChatView

class SingleChatPreview {
    @Preview(showBackground = true, name = "Empty chat")
    @Composable
    fun PreviewChatView() {
        MaterialTheme {
            SingleChatView(
                uiState = ChatUiState(isLoading = false, nomeChat = "Mario Rossi"),
                onSendClick = {},
                onBackClick = {},
                onHeaderClick = {},
                onPhotoClick = {}
            )
        }
    }

    @Preview(showBackground = true, name = "Filles")
    @Composable
    fun PreviewChatViewWithMesssages() {
        val randomPhoto = randomPhoto(LocalContext.current)
        MaterialTheme {
            SingleChatView(
                uiState = ChatUiState(
                    mioUserCode = "USER1",
                    isLoading = false,
                    nomeChat = "Mario Rossi",
                    messaggi = listOf(
                        ChatMessageDTO(
                            "CIAO",
                            null,
                            "USER1",
                            null,
                            null
                        ),
                        ChatMessageDTO(
                            "CIAO",
                            randomPhoto,
                            "USER2",
                            null,
                            null
                        )
                    )
                ),
                onSendClick = {},
                onBackClick = {},
                onHeaderClick = {},
                onPhotoClick = {}
            )
        }
    }
}