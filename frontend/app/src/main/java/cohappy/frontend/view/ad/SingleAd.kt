package cohappy.frontend.view.ad

import android.graphics.BitmapFactory
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import cohappy.frontend.components.CustomBackButton
import cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO
import cohappy.frontend.components.CustomAvatar
import cohappy.frontend.components.CustomChip
import cohappy.frontend.components.HousePosition

@Composable
fun SingleAdView(
    adDetail: GetHouseAdvertesimentDTO?,
    isLoading: Boolean,
    coverBmpBytes: ByteArray?,
    onBackClick: () -> Unit,
    onStartChatClick: (String) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF121212) else Color.White
    val contentColor = if (isDark) Color.White else Color.Black
    val bottomButtonColor = if (isDark) Color(0xFF3B3054) else Color(0xFF121212)

    val coverBmp: ImageBitmap? = remember(coverBmpBytes) {
        if (coverBmpBytes != null && coverBmpBytes.isNotEmpty()) {
            try {
                BitmapFactory.decodeByteArray(coverBmpBytes, 0, coverBmpBytes.size).asImageBitmap()
            } catch (e: Exception) { null }
        } else null
    }

    val displayTitle = if (!adDetail?.street.isNullOrBlank()) "Stanza in ${adDetail?.street}" else "Stanza singola"
    val street = adDetail?.street ?: ""
    val number = adDetail?.civicNumber?.toString() ?: ""
    val displayLoc = "$street $number".trim().ifBlank { "Posizione non specificata" }
    val displayPrice = "${adDetail?.costPerMonth?.toInt() ?: 0}€"

    val hostMail = adDetail?.publishedByEmail ?: ""
    val displayHost = if (hostMail.contains("@")) hostMail.substringBefore("@").replaceFirstChar { it.uppercase() } else "Host"
    val displayDesc = adDetail?.description ?: "Nessuna descrizione disponibile per questa stanza."

    Surface(modifier = Modifier.fillMaxSize(), color = bgColor, contentColor = contentColor) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6B53A4))
            }
        } else if (adDetail == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Annuncio non trovato", color = Color.Gray, fontSize = 18.sp)
            }
            CustomBackButton(color = MaterialTheme.colorScheme.primary, onClick = onBackClick, Modifier.padding(top = 48.dp))
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
                            Image(bitmap = coverBmp, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
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
                        color = bgColor
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            AnnuncioDetailTitlePrice(titolo = displayTitle, posizione = displayLoc, prezzo = displayPrice)
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
                        .background(Brush.verticalGradient(colors = listOf(Color.Transparent, bgColor.copy(alpha = 0.9f), bgColor), startY = 0f))
                        .padding(24.dp)
                ) {
                    Button(
                        onClick = {
                            val hostCode = adDetail.publishedByCode ?: ""
                            onStartChatClick(hostCode)
                        },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = bottomButtonColor, contentColor = Color.White)
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

@Composable
fun AnnuncioDetailDescription(descrizione: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Descrizione", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp), color = MaterialTheme.colorScheme.onBackground)
        Text(text = descrizione, color = Color.Gray, lineHeight = 24.sp, fontSize = 16.sp)
    }
}

@Composable
fun AnnuncioDetailHost(nomeHost: String) {
    val isDark = isSystemInDarkTheme()
    val PublisherBannerColor = if (isDark) Color(0xFF2D2342) else Color(0xFFF3EDFF)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(PublisherBannerColor, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomAvatar(initial = nomeHost.take(1)) // Prende l'iniziale del nome!
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text("Pubblicato da:", color = Color.Gray, fontSize = 14.sp)
            Text(nomeHost, fontWeight = FontWeight.Black, fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        //Icon(imageVector = Icons.Default.Info, contentDescription = "Info", tint = Color.Gray)
    }
}

@Composable
fun AnnuncioDetailTitlePrice(titolo: String, posizione: String, prezzo: String) {
    val BadgeColor = Color(0xFF6B53A4) // Il tuo viola scuro

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 16.dp)) {
            // Chip "Disponibile subito"
            CustomChip(text = "Disponibile subito", bgColor = Color(0xFFEBE5F7), textColor = BadgeColor)
            Spacer(modifier = Modifier.height(12.dp))

            Text(text = titolo, fontSize = 32.sp, fontWeight = FontWeight.Black, lineHeight = 36.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))


            HousePosition(posizione)
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Icon(imageVector = Icons.Outlined.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(text = posizione, color = Color.Gray, fontSize = 16.sp)
//            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(text = "Prezzo", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp))
            Box(
                modifier = Modifier
                    .background(BadgeColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = prezzo, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun AnnuncioDetailComforts() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("I comfort", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp), color = MaterialTheme.colorScheme.onBackground)

        // Mockup dei comfort
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CustomChip(
                text = "Wi-Fi",
                bgColor = MaterialTheme.colorScheme.surfaceVariant,
                textColor = MaterialTheme.colorScheme.onSurface,
                icon = Icons.Default.CheckCircle
            )
            CustomChip(
                text = "Aria Condizionata",
                bgColor = MaterialTheme.colorScheme.surfaceVariant,
                textColor = MaterialTheme.colorScheme.onSurface,
                icon = Icons.Default.CheckCircle
            )
        }
    }
}