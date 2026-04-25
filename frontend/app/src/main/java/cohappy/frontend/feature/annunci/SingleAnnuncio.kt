package cohappy.frontend.feature.annunci

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cohappy.frontend.R
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.components.AnnuncioDetailDescription
import cohappy.frontend.components.AnnuncioDetailHost
import cohappy.frontend.components.AnnuncioDetailTitlePrice
import cohappy.frontend.components.CustomBackButton
import cohappy.frontend.model.dto.response.GetHouseAdvertesimentDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PaginaAnnuncioSingolo(
    annuncioId: String,
    userToken: String?, // 💅 Aggiungiamo il token qui
    onBackClick: () -> Unit,
    onChatClick: (String) -> Unit = {},
    onRequireLoginClick: () -> Unit = {}

) {

    val isDark = isSystemInDarkTheme()
    val BgColor = if (isDark) Color(0xFF121212) else Color.White
    val ContentColor = if (isDark) Color.White else Color.Black
    val BottomButtonColor = if (isDark) Color(0xFF3B3054) else Color(0xFF121212)

    val coroutineScope = rememberCoroutineScope()

    var annuncio by remember { mutableStateOf<GetHouseAdvertesimentDTO?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var coverBmp by remember { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(annuncioId) {
        withContext(Dispatchers.IO) {
            try {
                val response = ClientSingleton.houseApi.getHouseAdvertisement(annuncioId)
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    annuncio = data

                    val primaImg = data.images?.firstOrNull()
                    if (primaImg != null) {
                        try {
                            val bytes = when (primaImg) {
                                is String -> Base64.decode(primaImg as String, Base64.DEFAULT)
                                is ByteArray -> primaImg as ByteArray
                                else -> null
                            }
                            if (bytes != null) coverBmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
                        } catch(e: Exception) { println("Errore img: ${e.message}") }
                    }
                }
            } catch (e: Exception) {
                println("Errore di rete: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    val displayTitle = if (!annuncio?.street.isNullOrBlank()) "Stanza in ${annuncio?.street}" else "Stanza singola"
    val street = annuncio?.street ?: ""
    val number = annuncio?.civicNumber ?: ""
    val displayLoc = (street + " " + number).ifBlank { "Posizione non specificata" }
    val displayPrice = "${annuncio?.costPerMonth?.toInt() ?: 0}€"

    val hostMail = annuncio?.publishedByEmail ?: ""
    val displayHost = if (hostMail.contains("@")) hostMail.substringBefore("@").replaceFirstChar { it.uppercase() } else "Host"
    val displayDesc = annuncio?.description ?: "Nessuna descrizione disponibile per questa stanza."

    Surface(modifier = Modifier.fillMaxSize(), color = BgColor, contentColor = ContentColor) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6B53A4))
            }
        } else if (annuncio == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Annuncio non trovato", color = Color.Gray, fontSize = 18.sp)
            }
            CustomBackButton(
                color = MaterialTheme.colorScheme.primary,
                onClick = onBackClick,
                Modifier.padding(top = 48.dp)
            )
        } else {
            Box(modifier = Modifier.fillMaxSize()) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 120.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(360.dp)) {
                        if (coverBmp != null) {
                            Image(bitmap = coverBmp!!, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        } else {
                            Image(painter = painterResource(id = R.drawable.casa1), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        }

                        Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent), endY = 200f)))

                        Box(
                            modifier = Modifier
                                .padding(start = 16.dp, top = 48.dp)
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f))
                                .clickable { onBackClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "Indietro", tint = Color.Black)
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-40).dp),
                        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                        color = BgColor
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            AnnuncioDetailTitlePrice(
                                titolo = displayTitle,
                                posizione = displayLoc,
                                prezzo = displayPrice
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            AnnuncioDetailHost(nomeHost = displayHost)
                            Spacer(modifier = Modifier.height(32.dp))
                            AnnuncioDetailDescription(descrizione = displayDesc)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, BgColor.copy(alpha = 0.9f), BgColor),
                                startY = 0f
                            )
                        )
                        .padding(24.dp)
                ) {
                    Button(
                        onClick = {
                            val hostUserCode = annuncio?.publishedByCode?.trim() ?: ""
                            val myUserCode = userToken?.replace("\"", "")?.trim() ?: ""

                            Log.d("TAG_CHECK_CHAT", "🕵️ Controllo pre-chat. Inserzionista: '$hostUserCode', Io: '$myUserCode'")

                            if (myUserCode.isBlank()) {
                                Log.w("TAG_CHECK_CHAT", "🛑 Utente OSPITE! Lo spedisco al login.")
                                onRequireLoginClick()}
                            else if (hostUserCode.isBlank()) {
                                Log.e("TAG_CHECK_CHAT", "❌ ERRORE: publishedByCode dell'annuncio è VUOTO o NULL! L'API non ce l'ha dato.")
                            } else if (hostUserCode == myUserCode) {
                                Log.e("TAG_CHECK_CHAT", "❌ ERRORE: Non puoi chattare con te stesso! Questo è un tuo annuncio.")
                            } else {
                                Log.d("TAG_CHECK_CHAT", "✅ Check superato! Navigo verso la chat passando l'host: $hostUserCode")
                                onChatClick(hostUserCode)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BottomButtonColor,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Outlined.ChatBubbleOutline, contentDescription = "Chat", modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Scrivi a $displayHost", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}