package cohappy.frontend.feature.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cohappy.frontend.components.ChatHeader
import cohappy.frontend.components.MessageBubble

@Composable
fun ChatAnnunci(chatCode: String,
                userToken: String?,
                onBackClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color.Black else Color.White
    val inputBgColor = if (isDark) Color.DarkGray else Color(0xFFF0F0F0)

    // 💅 Stato per quello che scriviamo nella barra
    var textInput by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // 1. HEADER (Il componente che abbiamo fatto prima!)
        ChatHeader(
            nomeUtente = "Anna (Host)",
            titoloAnnuncio = "Stanza singola luminosa",
            profileBitmap = null,
            onBackClick = onBackClick
        )

        // 2. LISTA DEI MESSAGGI (Si prende tutto lo spazio in mezzo)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                MessageBubble(
                    textMessage = "Ciao beddy! È libera la stanza?",
                    isMe = true
                )
            }

            item {
                MessageBubble(
                    textMessage = "Slayyyy! Certo che sì, vieni a vederla?",
                    isMe = false
                )
            }
        }

        // 3. BARRA DI INPUT (Fissata in basso)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
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
                maxLines = 3 // Così se scrivi tanto non ti copre tutto lo schermo!
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Bottone Invia


        }
    }
}

