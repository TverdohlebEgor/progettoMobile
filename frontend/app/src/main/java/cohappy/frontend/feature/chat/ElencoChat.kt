package cohappy.frontend.feature.chat

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import cohappy.frontend.components.ResearchBar
import cohappy.frontend.components.Titoli
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.components.ChatListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


data class ChatItem(val id: String?, val nome: String, val ultimoMessaggio: String, val orario: String)

@Composable
fun ElencoChat(
    userToken: String?,
    onChatClick: (Int) -> Unit = {}
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

                // Usiamo un try-catch per sicurezza. Se la rotta si chiama in modo diverso, aggiusta il nome!
                val response = ClientSingleton.chatApi.getUserChats(tokenPulito)

                if (response.isSuccessful && response.body() != null) {
                    // Se ci risponde, trasformiamo i suoi DTO nei nostri ChatItem per la grafica!
                    val chatDalDb = response.body()!!

                    chatsList = chatDalDb.map { getChatDto ->
                        // Sostituisci "getChatDto.nome" ecc. con le vere variabili che Egor ti manda nel GetChatDTO
                        ChatItem(
                            id = getChatDto.chatCode,
                            nome = getChatDto.name ?: "Chat senza nome", // Oppure il nome dell'altro utente
                            ultimoMessaggio = "Ultimo messaggio...", // Da collegare all'ultimo messaggio
                            orario = ""
                        )
                    }
                } else {
                    println("Errore dal server: ${response.code()}")
                    chatsList = emptyList() // Se fallisce, lista vuota
                }

            } catch (e: Exception) {
                println("Errore di rete durante il caricamento chat: ${e.message}")
                chatsList = emptyList()
            } finally {
                // Finito di caricare (sia bene che male), togliamo la rotellina!
                isLoading = false
            }
        }
    }

    // Filtriamo la lista in base alla ricerca (funziona anche col DB!)
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
            contentPadding = PaddingValues(bottom = 120.dp) // Spazio per la BottomBar
        ) {
            // 1. TITOLO
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Titoli(
                        titolo1 = "Chat",
                        color = ContentColor
                    )
                }
            }

            // 2. BARRA DI RICERCA (Sticky)
            stickyHeader {
                Surface(
                    color = BgColor,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .padding(top = 8.dp, bottom = 16.dp)
                    ) {
                        ResearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            placeholder = "Cerca nelle chat..."
                        )
                    }
                }
            }

            // 3. ELENCO DELLE CHAT O STATO VUOTO
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Caricamento chat...", color = Color.Gray, fontSize = 16.sp)
                    }
                }
            } else if (annunciFiltrati.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        contentAlignment = Alignment.Center
                    ) {
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
                    ChatListItem(chat = chat, onClick = { onChatClick(chat.id as Int) })
                }
            }
        }
    }
}