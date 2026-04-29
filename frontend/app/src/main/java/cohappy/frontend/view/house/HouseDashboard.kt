package cohappy.frontend.view.house

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cohappy.frontend.components.CustomIconButton
import cohappy.frontend.components.HousePosition
import cohappy.frontend.components.ProfileAvatar
import cohappy.frontend.components.Titoli
import cohappy.frontend.model.NextChore
import cohappy.frontend.model.Notification
import cohappy.frontend.model.TotalDebt

@Composable
fun HomeGestionaleView(
    nomeUtente: String,
    imageBytes: ByteArray?,
    isLoading: Boolean,
    onAddClick: () -> Unit,
    onChoreClick: (String) -> Unit,
    onWalletClick: (String) -> Unit,
    notifications: List<Notification>,
    nextChore: NextChore?,
    totalDebt: TotalDebt?,
    userToken: String
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

                    SummRow(
                        modifier = Modifier.fillMaxWidth(),
                        nameChore = nextChore?.choreName ?: "Nessuna",
                        debtAmount = totalDebt?.totalDebt ?: 0.0
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    UltimoAggName(onClick = {})

                    LastMessView(
                        isLoading = isLoading,
                        notification = notifications,
                        onChatClick = { },
                        modifier = Modifier.weight(1f)
                    )
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
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val isDark = isSystemInDarkTheme()
    val contentColor = if (isDark) Color.White else Color.Black

    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = backgroundColor,
        shadowElevation = if (isDark) 0.dp else 10.dp,
        border = if (isDark) BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)) else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            // Icona tonda
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
                color = contentColor.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = amount,
                color = contentColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SummRow(modifier: Modifier = Modifier, nameChore: String, debtAmount: Double, onWalletClick: () -> Unit = {}, onChoreClick: () -> Unit = {}){
    val isDark = isSystemInDarkTheme()

    val box1Bg = if (isDark) Color(0xFF1E1C22) else Color.White
    val box1ContentColor = if (isDark) Color.White else Color.Black
    val box1IconBg = if (isDark) Color(0xFF4A1C1C) else MaterialTheme.colorScheme.errorContainer
    val box1IconColor = if (isDark) Color(0xFFFF6961) else MaterialTheme.colorScheme.error

    val box2Bg = MaterialTheme.colorScheme.onPrimary
    val box2ContentColor = Color.White
    val box2IconBg = MaterialTheme.colorScheme.primary
    val box2IconColor = if(isDark) Color.Black else Color.White

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ){
        SummBox(
            iconColor = box1IconColor,
            iconBackColor = box1IconBg,
            iconLabel = "wallet",
            icon = Icons.Default.Wallet,
            backgroundColor = box1Bg,
            title = "Da dare",
            amount = "${String.format("%.2f", debtAmount)}€",
            modifier = Modifier.weight(1f),
            onClick = onWalletClick
        )
        SummBox(
            iconColor = box2IconColor,
            iconBackColor = box2IconBg,
            iconLabel = "chore",
            icon = Icons.Default.WaterDrop,
            backgroundColor = box2Bg,
            title = "Tocca a te",
            amount = nameChore ,
            modifier = Modifier.weight(1f),
            onClick = onChoreClick
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
fun LastMess(modifier: Modifier = Modifier, title: String, subTitle: String, type: String, image: ImageBitmap? = null){
    val isDark = isSystemInDarkTheme()
    val contentColor = if (isDark) Color.White else Color.Black

    val box1IconBg = if (isDark) Color(0xFF4A1C1C) else MaterialTheme.colorScheme.errorContainer
    val box1IconColor = if (isDark) Color(0xFFFF6961) else MaterialTheme.colorScheme.error

    val box2IconBg = MaterialTheme.colorScheme.primary
    val box2IconColor = if(isDark) Color.Black else Color.White

    val bgColor = if(type=="CHAT")MaterialTheme.colorScheme.primaryContainer else if(type=="PORTFOLIO") MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(32.dp),
        color = bgColor,
        shadowElevation = 10.dp

    ){
        Row(modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween  , verticalAlignment = Alignment.CenterVertically){

            if (type == "CHAT") {
                ProfileAvatar(size = 40, imageBitmap = image)
            } else {
                if(type=="CHORE"){
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(box2IconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            tint =box2IconColor,
                            contentDescription = "ultime faccende",
                            imageVector = Icons.Default.WaterDrop,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                else{
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(box1IconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            tint = box1IconColor,
                            contentDescription = "ultime spese",
                            imageVector = Icons.Default.Wallet,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = contentColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
                Text(
                    text = subTitle,
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

@Composable
fun LastMessView(
    isLoading: Boolean,
    notification: List<Notification>,
    onChatClick: (String) -> Unit,
    modifier: Modifier
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 0.dp, end = 0.dp, bottom = 120.dp, top = 8.dp)
    ) {

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp), contentAlignment = Alignment.Center
                ) {
                    Text("Caricamento chat...", color = Color.Gray, fontSize = 16.sp)
                }
            }
        } else {
            items(notification) { chat ->
                LastMess(title = chat.title, subTitle = chat.subtitle, type = chat.eventType)
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHouseDashboard() {
    val finteNotifiche = listOf(
        Notification(
            eventId = "1", eventType = "CHAT", title = "Marco",
            subtitle = "Ciao, usciamo?", timestamp = "Ora", userCode = "test-token"
        ),
        Notification(
            eventId = "2", eventType = "CHORE", title = "Fornello cucina",
            subtitle = "Tocca a te", timestamp = "Oggi", userCode = "test-token"
        )
    )

    val fintoDebitoTot = TotalDebt(totalDebt = 45.50)
    val fintoProxFaccenda = NextChore(choreId = "1", choreName = "Fornello cucina", choreDate = "2026-04-28T18:30:00")

    MaterialTheme {
        HomeGestionaleView(
            nomeUtente = "Ale",
            imageBytes = null,
            isLoading = false,
            userToken = "test-token",
            onAddClick = {},
            notifications = finteNotifiche,
            nextChore = fintoProxFaccenda,
            totalDebt = fintoDebitoTot,
            onChoreClick = {},
            onWalletClick = {  }
        )
    }
}

