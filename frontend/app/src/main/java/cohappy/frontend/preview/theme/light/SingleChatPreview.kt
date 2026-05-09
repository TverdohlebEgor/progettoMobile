package cohappy.frontend.preview.theme.light

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.client.dto.response.ChatMessageDTO
import cohappy.frontend.util.randomPhoto
import cohappy.frontend.view.chat.ChatInput
import cohappy.frontend.view.chat.SingleChatView
import cohappy.frontend.viewmodel.ChatUiState

@Preview(showBackground = true, name = "Empty chat")
@Composable
fun PreviewChatView() {
    MaterialTheme {
        SingleChatView(
            uiState = ChatUiState(
                isLoading = false,
                nomeChat = "Mario Rossi"
            ),
            onSendClick = { _, _ -> },
            onBackClick = {},
            onHeaderClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
fun PreviewChatViewLoading() {
    MaterialTheme {
        SingleChatView(
            uiState = ChatUiState(
                isLoading = true,
                nomeChat = "Mario Rossi"
            ),
            onSendClick = { _, _ -> },
            onBackClick = {},
            onHeaderClick = {}
        )
    }
}

@Preview(showBackground = true, name = "With Messages")
@Composable
fun PreviewChatViewWithMesssages() {
    val context = LocalContext.current
    val randomPhoto = randomPhoto(context)
    MaterialTheme {
        SingleChatView(
            uiState = ChatUiState(
                mioUserCode = "USER1",
                isLoading = false,
                nomeChat = "Mario Rossi",
                immagineChat = randomPhoto,
                messaggi = listOf(
                    ChatMessageDTO(
                        message = "Ciao, come stai?",
                        userCode = "USER1"
                    ),
                    ChatMessageDTO(
                        message = "Tutto bene, tu?",
                        userCode = "USER2"
                    )
                )
            ),
            onSendClick = { _, _ -> },
            onBackClick = {},
            onHeaderClick = {}
        )
    }
}

@Preview(showBackground = true, name = "With Image Message")
@Composable
fun PreviewChatViewWithImageMessage() {
    val context = LocalContext.current
    val randomPhoto = randomPhoto(context)
    MaterialTheme {
        SingleChatView(
            uiState = ChatUiState(
                mioUserCode = "USER1",
                isLoading = false,
                nomeChat = "Mario Rossi",
                immagineChat = randomPhoto,
                messaggi = listOf(
                    ChatMessageDTO(
                        message = "Guarda questa foto!",
                        messageImage = randomPhoto,
                        userCode = "USER1"
                    ),
                    ChatMessageDTO(
                        message = "Wow!",
                        userCode = "USER2"
                    )
                )
            ),
            onSendClick = { _, _ -> },
            onBackClick = {},
            onHeaderClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Many Messages")
@Composable
fun PreviewChatViewWithALotMesssages() {
    val context = LocalContext.current
    val randomPhoto = randomPhoto(context)
    val message = ChatMessageDTO(
        message = "Messaggio di test",
        userCode = "USER1"
    )
    val message2 = message.copy(userCode = "USER2", message = "Risposta")
    MaterialTheme {
        SingleChatView(
            uiState = ChatUiState(
                mioUserCode = "USER1",
                isLoading = false,
                nomeChat = "Mario Rossi",
                immagineChat = randomPhoto,
                messaggi = List(20) { index ->
                    if (index % 2 == 0) message else message2
                }
            ),
            onSendClick = { _, _ -> },
            onBackClick = {},
            onHeaderClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Chat Input - Photo Selected")
@Composable
fun PreviewChatInputWithPhoto() {
    val context = LocalContext.current
    val randomPhoto = randomPhoto(context)
    MaterialTheme {
        Column(modifier = Modifier.fillMaxWidth()) {
            ChatInput(
                onSendClick = { _, _ -> },
                onPhotoClick = {},
                initialSelectedImage = randomPhoto,
                initialTextInput = "Guarda che bella questa casa!"
            )
        }
    }
}

@Preview(showBackground = true, name = "Chat Input - Menu Open")
@Composable
fun PreviewChatInputMenuOpen() {
    MaterialTheme {
        Column(modifier = Modifier.fillMaxWidth()) {
            ChatInput(
                onSendClick = { _, _ -> },
                onPhotoClick = {},
                initialShowMenu = true
            )
        }
    }
}
