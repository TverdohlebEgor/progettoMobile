package cohappy.frontend.feature.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cohappy.frontend.components.ChatHeader
import cohappy.frontend.components.MessageBubble
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.model.dto.response.ChatMessageDTO
import cohappy.frontend.model.dto.request.AddMessageDTO
import cohappy.frontend.model.dto.request.CreateChatDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ChatAnnunci(
    chatCode: String,
    userToken: String?,
    onBackClick: () -> Unit,
    onNavigateToAnnuncio: (String) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color.Black else Color.White
    val inputBgColor = if (isDark) Color.DarkGray else Color(0xFFF0F0F0)

    val coroutineScope = rememberCoroutineScope()
    val mioUserCode = userToken?.replace("\"", "")?.trim() ?: ""

    var textInput by remember { mutableStateOf("") }
    var messaggi by remember { mutableStateOf<List<ChatMessageDTO>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var nomeChat by remember { mutableStateOf("Caricamento...") }
    var resolvedChatCode by remember { mutableStateOf("") }
    var resolvedAnnuncioId by remember { mutableStateOf("") }

    var sottotitoloChat by remember { mutableStateOf("") }

    LaunchedEffect(chatCode) {
        Log.d("TAG_CHAT", "🚀 Inizio flusso. Parametro passato: $chatCode")
        isLoading = true

        var nomeMalcapitato = "Sconosciuto"
        var idChatDaUsare = ""
        var chatGiaEsistente = false
        var otherUserCodeForSearch = chatCode +


        try {
            Log.d("TAG_CHAT", "🔍 1. Recupero nome del malcapitato (ID: $chatCode)")
            val profileResp = withContext(Dispatchers.IO) { ClientSingleton.userApi.getUserProfile(chatCode) }

            if (profileResp.isSuccessful && profileResp.body() != null) {
                val user = profileResp.body()!!
                val fullName = "${user.name ?: ""} ${user.surname ?: ""}".trim()
                if (fullName.isNotBlank()) {
                    nomeMalcapitato = fullName
                }
                Log.d("TAG_CHAT", "✅ 1. Nome recuperato: $nomeMalcapitato")
            } else {
                Log.d("TAG_CHAT", "⚠️ 1. Profilo non trovato o parametro è un ID Chat. Uso nome Sconosciuto.")
            }
        } catch (e: Exception) {
            Log.e("TAG_CHAT", "🚨 1. Errore API profilo: ${e.message}")
        }


        try {
            Log.d("TAG_CHAT", "🔍 2. Cerco le chat dell'utente loggato")
            val chatsResp = withContext(Dispatchers.IO) { ClientSingleton.chatApi.getUserChats(mioUserCode) }
            val mieChats = chatsResp.body() ?: emptyList()

            // Cerchiamo se abbiamo la chat!
            val chatTrovata = mieChats.find {
                it.chatCode == chatCode ||
                        (it.participating != null && it.participating!!.contains(mioUserCode) && it.participating!!.contains(chatCode) && it.participating!!.size == 2)
            }

            if (chatTrovata != null) {
                Log.d("TAG_CHAT", "✅ 2. Chat trovata! ID: ${chatTrovata.chatCode}")
                idChatDaUsare = chatTrovata.chatCode ?: ""
                chatGiaEsistente = true

                if (nomeMalcapitato == "Sconosciuto" && !chatTrovata.name.isNullOrBlank()) {
                    nomeMalcapitato = chatTrovata.name!!
                }


                val trovatoAltro = chatTrovata.participating?.find { it != mioUserCode }
                if (trovatoAltro != null) {
                    otherUserCodeForSearch = trovatoAltro
                }
            } else {

                Log.d("TAG_CHAT", "🔨 2. Chat non trovata. Procedo con la CREAZIONE.")

                val nomeDaSalvare = if (nomeMalcapitato == "Sconosciuto") "Nuova Chat" else nomeMalcapitato
                val createDto = CreateChatDTO(
                    participating = listOf(mioUserCode, chatCode),
                    name = nomeDaSalvare,
                    immage = null
                )

                val createResp = withContext(Dispatchers.IO) { ClientSingleton.chatApi.createChat(createDto) }

                if (createResp.isSuccessful && createResp.body() != null) {
                    idChatDaUsare = createResp.body()!!.replace("\"", "").trim()
                    chatGiaEsistente = false
                    Log.d("TAG_CHAT", "✅ 2. Chat creata con successo! Nuovo ID: $idChatDaUsare")
                } else {
                    Log.e("TAG_CHAT", "❌ 2. Fallita creazione chat sul DB. Codice Egor: ${createResp.code()}")
                }
            }
        } catch (e: Exception) {
            Log.e("TAG_CHAT", "🚨 2. Errore API ricerca/creazione: ${e.message}")
        }

        // 💅 BLOCCO 2.5: CERCHIAMO L'ANNUNCIO DELL'HOST!
        try {
            Log.d("TAG_CHAT", "🔍 2.5 Cerco se l'utente $otherUserCodeForSearch ha un annuncio attivo")
            val adsResp = withContext(Dispatchers.IO) { ClientSingleton.houseApi.getAllHouseAdvertisements() }
            if (adsResp.isSuccessful && adsResp.body() != null) {
                // Cerchiamo il primo annuncio pubblicato da questa persona
                val foundAd = adsResp.body()!!.find { it.publishedByCode == otherUserCodeForSearch }
                if (foundAd != null) {
                    resolvedAnnuncioId = foundAd.houseCode ?: ""
                    Log.d("TAG_CHAT", "✅ 2.5 Annuncio trovato! ID: $resolvedAnnuncioId")
                } else {
                    Log.d("TAG_CHAT", "⚠️ 2.5 Nessun annuncio trovato per questo utente.")
                }
            }
        } catch (e: Exception) {
            Log.e("TAG_CHAT", "🚨 2.5 Errore ricerca annuncio: ${e.message}")
        }

        // 💅 BLOCCO 3: APPLICHIAMO ALLA GRAFICA
        if (idChatDaUsare.isNotBlank()) {
            resolvedChatCode = idChatDaUsare
            nomeChat = nomeMalcapitato

            if (chatGiaEsistente) {
                // SCARICA I MESSAGGI (LA CARICA E BONA)
                try {
                    Log.d("TAG_CHAT", "📥 3. Chat esistente. Scarico i messaggi...")
                    val msgResp = withContext(Dispatchers.IO) { ClientSingleton.chatApi.getMessages(idChatDaUsare) }

                    if (msgResp.isSuccessful && msgResp.body() != null) {
                        messaggi = msgResp.body()!!
                        Log.d("TAG_CHAT", "✅ 3. Trovati ${messaggi.size} messaggi.")
                        if (messaggi.isEmpty()) sottotitoloChat = "Inizia la conversazione"
                    } else {
                        Log.e("TAG_CHAT", "❌ 3. Errore scaricamento messaggi: ${msgResp.code()}")
                    }
                } catch (e: Exception) {
                    Log.e("TAG_CHAT", "🚨 3. Errore Moshi/Rete nei messaggi: ${e.message}")
                    sottotitoloChat = "Messaggi vecchi non disponibili"
                }
            } else {
                // CHAT APPENA CREATA (NON CARICA I MESSAGGI E ABBIAMO FATTO)
                Log.d("TAG_CHAT", "🛑 3. Chat nuova. NON carico i messaggi.")
                messaggi = emptyList()
                sottotitoloChat = "Inizia la conversazione"
            }
        } else {
            nomeChat = "Errore"
            sottotitoloChat = "Impossibile aprire la chat"
        }

        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(top=48.dp, bottom=24.dp)
    ) {
        ChatHeader(
            nomeUtente = nomeChat,
            profileBitmap = null,
            onBackClick = onBackClick,
            onHeaderClick = {

                if (resolvedAnnuncioId.isNotBlank()) {
                    onNavigateToAnnuncio(resolvedAnnuncioId)
                }
            }
        )

        if (isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6B53A4))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(messaggi) { msg ->
                    val sonoIo = (msg.userCode == mioUserCode)
                    MessageBubble(
                        textMessage = msg.message ?: "",
                        isMe = sonoIo
                    )
                }
            }
        }

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
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(
                onClick = {
                    if (textInput.isNotBlank() && resolvedChatCode.isNotBlank()) {
                        val testoDaInviare = textInput
                        textInput = ""

                        val nuovoMsgFinto = ChatMessageDTO(
                            message = testoDaInviare,
                            userCode = mioUserCode
                        )
                        messaggi = messaggi + nuovoMsgFinto

                        coroutineScope.launch {
                            try {
                                Log.d("TAG_CHAT", "📤 Invio messaggio alla chat: $resolvedChatCode")
                                val pacchetto = AddMessageDTO(
                                    message = testoDaInviare,
                                    userCode = mioUserCode,
                                    chatCode = resolvedChatCode
                                )
                                val response = withContext(Dispatchers.IO) {
                                    ClientSingleton.chatApi.addMessage(pacchetto)
                                }
                                if (response.isSuccessful) {
                                    Log.d("TAG_CHAT", "✅ Messaggio inviato!")
                                } else {
                                    Log.e("TAG_CHAT", "❌ Errore invio: ${response.code()}")
                                }
                            } catch (e: Exception) {
                                Log.e("TAG_CHAT", "🚨 Errore Moshi/Rete nell'invio: ${e.message}")
                            }
                        }
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
}