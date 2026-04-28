package cohappy.frontend.view.house

import android.R.attr.top
import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cohappy.frontend.components.CustomIconButton
import cohappy.frontend.components.HousePosition
import cohappy.frontend.components.ProfileAvatar
import cohappy.frontend.components.ResearchBar
import cohappy.frontend.components.Titoli
import cohappy.frontend.model.ChatListItem
import cohappy.frontend.view.chat.ChatListItemRow

@Composable
fun HomeGestionaleView(
    nomeUtente: String,
    imageBytes: ByteArray?,
    isLoading: Boolean,
    onAddClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color.Black else Color.White
    val contentColor = if (isDark) Color.White else Color.Black


    val profileBitmap: ImageBitmap? = remember(imageBytes) {
        if (imageBytes != null && imageBytes.isNotEmpty()) {
            try {
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size).asImageBitmap()
            } catch (e: Exception) { null }
        } else null
    }

    Surface(modifier = Modifier.fillMaxSize(), color = bgColor, contentColor = contentColor) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6B53A4))
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                ) {
                    HousePosition("La tua Casa")

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            Titoli(
                                titolo1 = "Ciao! $nomeUtente",
                                color = contentColor,
                                paddingTop = 16.dp

                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        ProfileAvatar(imageBitmap = profileBitmap, size = 64)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    SummRow()

                    Spacer(modifier = Modifier.height(24.dp))

                    UltimoAggName(onClick = {})

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 120.dp)
                    ) {
                        item {
                            LastMess(
                                backgroundColor = MaterialTheme.colorScheme.onSecondary
                            )
                        }

                        item {
                            LastMess(
                                backgroundColor = MaterialTheme.colorScheme.onSecondary
                            )
                        }

                        item {
                            LastMess(
                                backgroundColor = MaterialTheme.colorScheme.onSecondary
                            )
                        }

                        item {
                            LastMess(
                                backgroundColor = MaterialTheme.colorScheme.onSecondary
                            )
                        }

                        item {
                            LastMess(
                                backgroundColor = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }

                CustomIconButton(
                    icon = Icons.Default.Add,
                    onClick = onAddClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp, bottom = 120.dp)
                )
            }
        }
    }
}

@Composable
fun SummBox(
    title: String,
    amount: String,
    iconColor: Color,
    iconBackColor: Color,
    iconLabel: String,
    icon: ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val textColor = if (isDark) Color.White else Color.Black


    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = backgroundColor,
        shadowElevation = 10.dp

    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(iconBackColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = iconLabel, tint = iconColor, modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                color = if (isDark) Color.LightGray else Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = amount,
                color = textColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun SummRow(modifier: Modifier = Modifier){
    val IconColorWater = if (isSystemInDarkTheme()) Color.Black else Color.White
    val BgColor = if(isSystemInDarkTheme())Color.Gray else Color.White


    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ){
        SummBox(
            iconColor = MaterialTheme.colorScheme.error,
            iconBackColor = MaterialTheme.colorScheme.errorContainer,
            iconLabel = "wallet",
            icon = Icons.Default.Wallet,
            backgroundColor = BgColor,
            title = "Da dare",
            amount = "15,00 €",
            modifier = Modifier.weight(1f)
        )
        SummBox(
            iconColor = IconColorWater,
            iconBackColor = MaterialTheme.colorScheme.primary ,
            iconLabel = "chore",
            icon = Icons.Default.WaterDrop,
            backgroundColor = MaterialTheme.colorScheme.onPrimary,
            title = "Tocca a te",
            amount = "Bagno",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun UltimoAggName(onClick: () -> Unit = {}){
    val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black

    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick),horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
        Text(
            text = "Ultimi Aggiornamenti",
            color = textColor,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Start,
            fontSize = 16.sp,
            lineHeight = 46.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            tint = textColor,
            contentDescription = "ultimi aggiornamenti",
            imageVector = Icons.Default.ArrowForwardIos,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun LastMess(modifier: Modifier = Modifier, backgroundColor: Color){
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color.Black else Color.White
    val contentColor = if (isDark) Color.White else Color.Black

    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = RoundedCornerShape(32.dp),
        color = backgroundColor,
        shadowElevation = 10.dp

    ){
        Row(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween  , verticalAlignment = Alignment.CenterVertically){
            ProfileAvatar(size=40)

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Nome Utente o categoria",
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
                Text(
                    text = "Ultimo messaggio",
                    color = if (isDark) Color.LightGray else Color.Gray,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start,
                    fontSize = 14.sp,
                    lineHeight = 24.sp
                )
            }
        }
    }
}


/**
 * Capire come mettere i messaggi e le notifiche delle varie parti
 */
@Composable
fun LastMessView(
    isLoading: Boolean,
    chats: List<ChatListItem>,
    onChatClick: (String) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color.Black else Color.White
    val contentColor = if (isDark) Color.White else Color.Black

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor,
        contentColor = contentColor
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {

            if (isLoading) {
                item {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp), contentAlignment = Alignment.Center) {
                        Text("Caricamento chat...", color = Color.Gray, fontSize = 16.sp)
                    }
                }
            } else {
                items(chats) { chat ->
                    LastMess(backgroundColor = Color.LightGray)
                }
            }
        }
    }
}
