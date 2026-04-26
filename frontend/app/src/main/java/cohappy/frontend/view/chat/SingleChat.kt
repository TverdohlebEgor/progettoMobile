package cohappy.frontend.view.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cohappy.frontend.R
import cohappy.frontend.client.dto.response.ChatMessageDTO
import cohappy.frontend.components.MessageBubble
import cohappy.frontend.model.ChatUiState

@Composable
fun SingleChatView(
    uiState: ChatUiState,
    onSendClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onHeaderClick: () -> Unit
) {
    val bgColor = MaterialTheme.colorScheme.background

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(top = 48.dp, bottom = 24.dp)
    ) {
        ChatHeader(
            nomeUtente = uiState.nomeChat,
            onBackClick = onBackClick,
            onHeaderClick = onHeaderClick
        )

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else {
                MessageList(uiState.messaggi, uiState.mioUserCode)
            }
        }

        ChatInput(onSendClick)
    }
}

@Composable
private fun MessageList(messages: List<ChatMessageDTO>, myUserCode: String?) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 0.dp)
    ) {
        items(messages) { msg ->
            MessageBubble(
                textMessage = msg.message ?: "",
                isMe = msg.userCode == myUserCode
            )
        }
    }
}

@Composable
private fun ChatInput(onSendClick: (String) -> Unit) {
    var textInput by remember { mutableStateOf("") }
    val inputBgColor = if (isSystemInDarkTheme()) Color.DarkGray else Color(0xFFF0F0F0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextField(
            value = textInput,
            onValueChange = { textInput = it },
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = inputBgColor,
                unfocusedContainerColor = inputBgColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            placeholder = { Text("Scrivi un messaggio...") },
            maxLines = 3
        )

        IconButton(
            onClick = {
                if (textInput.isNotBlank()) {
                    onSendClick(textInput)
                    textInput = ""
                }
            },
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary, CircleShape)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Invia",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun ChatHeader(
    nomeUtente: String,
    profileBitmap: ImageBitmap? = null,
    onBackClick: () -> Unit,
    onHeaderClick: () -> Unit = {}
) {
    val contentColor = MaterialTheme.colorScheme.onBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                contentDescription = "Torna indietro",
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .clickable { onHeaderClick() }
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileImage(profileBitmap, nomeUtente)

            Text(
                text = nomeUtente,
                color = contentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ProfileImage(bitmap: ImageBitmap?, name: String) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Foto di $name",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.default_photo_profile),
                contentDescription = "Foto di default",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview(showBackground = true, name = "Empty chat")
@Composable
fun PreviewChatView() {
    MaterialTheme {
        SingleChatView(
            uiState = ChatUiState(isLoading = false, nomeChat = "Mario Rossi"),
            onSendClick = {},
            onBackClick = {},
            onHeaderClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Filles")
@Composable
fun PreviewChatViewWithMesssages() {
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
                        null,
                        "USER2",
                        null,
                        null
                    )
                )
            ),
            onSendClick = {},
            onBackClick = {},
            onHeaderClick = {}
        )
    }
}