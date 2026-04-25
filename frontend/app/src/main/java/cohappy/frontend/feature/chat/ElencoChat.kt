package cohappy.frontend.feature.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import cohappy.frontend.components.ResearchBar
import cohappy.frontend.components.Titoli
import cohappy.frontend.client.ClientSingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Struttura dati PERFETTA a prova di bomba (solo String)
data class ChatItem(val id: String, val nome: String, val ultimoMessaggio: String, val orario: String)


@Composable
fun InternalChatListItem(chat: ChatItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = chat.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(text = chat.ultimoMessaggio, color = Color.Gray, fontSize = 14.sp, maxLines = 1)
        }

        Text(text = chat.orario, color = Color.Gray, fontSize = 12.sp)
    }
}


@Composable
fun ElencoChat(
    userToken: String?,
    onChatClick: (String) -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    val BgColor = if (isDark) Color.Black else Color.White
    val ContentColor = if (isDark) Color.White else Color.Black

    var searchQuery by remember { mutableStateOf("") }
    var chatsList by remember { mutableStateOf<List<ChatItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userToken) {
        withContext(Dispatchers.IO) {
            try {
                val tokenPulito = userToken?.replace("\"", "")?.trim()
                val response = ClientSingleton.chatApi.getUserChats(tokenPulito)

                if (response.isSuccessful && response.body() != null) {
                    val chatDalDb = response.body()!!
                    chatsList = chatDalDb.map { getChatDto ->
                        ChatItem(
                            id = getChatDto.chatCode ?: "", // Sempre Stringa!
                            nome = getChatDto.name ?: "Chat senza nome",
                            ultimoMessaggio = "Tocca per aprire...",
                            orario = ""
                        )
                    }
                } else {
                    chatsList = emptyList()
                }
            } catch (e: Exception) {
                chatsList = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    val annunciFiltrati = chatsList.filter { chat ->
        chat.nome.contains(searchQuery, ignoreCase = true) ||
                chat.ultimoMessaggio.contains(searchQuery, ignoreCase = true)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BgColor,
        contentColor = ContentColor
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Titoli(titolo1 = "Chat", color = ContentColor)
                }
            }

            stickyHeader {
                Surface(color = BgColor, modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(horizontal = 24.dp).padding(top = 8.dp, bottom = 16.dp)) {
                        ResearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            placeholder = "Cerca nelle chat..."
                        )
                    }
                }
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(250.dp), contentAlignment = Alignment.Center) {
                        Text("Caricamento chat...", color = Color.Gray, fontSize = 16.sp)
                    }
                }
            } else if (annunciFiltrati.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(250.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (searchQuery.isEmpty()) "Non hai chat attive" else "Nessuna chat trovata",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                items(annunciFiltrati) { chat ->

                    InternalChatListItem(
                        chat = chat,
                        onClick = { onChatClick(chat.id) }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), color = Color.Gray.copy(alpha = 0.2f))
                }
            }
        }
    }
}