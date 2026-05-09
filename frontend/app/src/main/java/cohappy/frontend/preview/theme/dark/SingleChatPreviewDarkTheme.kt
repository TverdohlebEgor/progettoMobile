package cohappy.frontend.preview.theme.dark

import android.content.res.Configuration
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

@Preview(showBackground = true, name = "Empty chat", uiMode = Configuration.UI_MODE_NIGHT_YES)
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

@Preview(showBackground = true, name = "Loading", uiMode = Configuration.UI_MODE_NIGHT_YES)
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

@Preview(showBackground = true, name = "With Messages", uiMode = Configuration.UI_MODE_NIGHT_YES)
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

@Preview(showBackground = true, name = "With Image Message", uiMode = Configuration.UI_MODE_NIGHT_YES)
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

@Preview(showBackground = true, name = "Many Messages", uiMode = Configuration.UI_MODE_NIGHT_YES)
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