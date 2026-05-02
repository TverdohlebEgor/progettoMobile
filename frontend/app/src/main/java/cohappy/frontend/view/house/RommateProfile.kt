package cohappy.frontend.view.house

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cohappy.frontend.components.LogoutTextButton
import cohappy.frontend.components.ProfileAvatar
import cohappy.frontend.components.Titoli
import cohappy.frontend.model.RoommateItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseProfileView(
    userName: String,
    userSurname: String,
    imageBytes: ByteArray?,
    isLoading: Boolean,
    houseAddress: String,
    houseCode: String,
    isUpdatingCode: Boolean = false,
    codeUpdateError: String? = null,
    onUpdateCodeClick: (String) -> Unit = {},
    // 💅 NUOVI PARAMETRI PER L'INDIRIZZO!
    isUpdatingAddress: Boolean = false,
    addressUpdateError: String? = null,
    onUpdateAddressClick: (String) -> Unit = {},
    onEditPhotoClick: () -> Unit = {},
    onLeaveHouseClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onRoommatesClick: () -> Unit = {},
    onPasswordChangeClick: () -> Unit = {},
    showRoommatesPopup: Boolean = false,
    roommatesList: List<RoommateItem> = emptyList(),
    isCurrentUserAdmin: Boolean = false,
    isRoommatesLoading: Boolean = false,
    onDismissRoommatesPopup: () -> Unit = {},
    onPromoteClick: (String) -> Unit = {},
    onKickClick: (String) -> Unit = {}
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = bgColor,
        contentColor = contentColor
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6B53A4))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Titoli(titolo1 = "Profilo", color = contentColor)

                ProfileHeaderCardHouse(
                    nome = userName,
                    cognome = userSurname,
                    profileBitmap = profileBitmap,
                    onEditPhotoClick = onEditPhotoClick
                )

                Spacer(modifier = Modifier.height(24.dp))

                HouseInfoCard(
                    houseAddress = houseAddress,
                    houseCode = houseCode,
                    isUpdatingCode = isUpdatingCode,
                    codeUpdateError = codeUpdateError,
                    onUpdateCodeClick = onUpdateCodeClick,
                    isUpdatingAddress = isUpdatingAddress,
                    addressUpdateError = addressUpdateError,
                    onUpdateAddressClick = onUpdateAddressClick,
                    onLeaveHouseClick = onLeaveHouseClick
                )

                Spacer(modifier = Modifier.height(32.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Impostazioni della casa",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // 💅 COME PROMESSO: REGOLE CANCELLATO, POLVERIZZATO, VAPORIZZATO. 💥

                    SettingItem(
                        icon = Icons.Default.Group,
                        title = "Coinquilini",
                        onClick = onRoommatesClick
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Impostazioni Account",
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    SettingItem(
                        icon = Icons.Default.Lock,
                        title = "Modifica password",
                        onClick = onPasswordChangeClick
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                LogoutButtonPremium(onClick = onLogoutClick)

                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }

    if (showRoommatesPopup) {
        val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val sheetBgColor = if (isDark) Color(0xFF1E1C22) else Color.White
        val sheetContentColor = if (isDark) Color.White else Color.Black

        ModalBottomSheet(
            onDismissRequest = onDismissRoommatesPopup,
            sheetState = sheetState,
            containerColor = sheetBgColor,
            dragHandle = {
                Box(modifier = Modifier.padding(top = 16.dp).width(40.dp).height(4.dp).background(Color.Gray.copy(alpha = 0.5f), CircleShape))
            }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "I tuoi coinquilini",
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp,
                    color = sheetContentColor
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (isRoommatesLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF6B53A4))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(roommatesList) { roommate ->
                            RoommateRow(
                                roommate = roommate,
                                isCurrentUserAdmin = isCurrentUserAdmin,
                                onPromoteClick = { onPromoteClick(roommate.userCode) },
                                onKickClick = { onKickClick(roommate.userCode) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeaderCardHouse(
    nome: String,
    cognome: String,
    profileBitmap: ImageBitmap?,
    onEditPhotoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF2D2342) else Color(0xFFEBE5F7)

    val nomeUp = nome.replaceFirstChar { it.uppercase() }
    val cognomeUp = cognome.replaceFirstChar { it.uppercase() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(bgColor)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileAvatar(imageBitmap = profileBitmap, size = 100)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$nomeUp $cognomeUp".trim(),
                fontWeight = FontWeight.Black,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onEditPhotoClick,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6B53A4).copy(alpha = 0.8f),
                    contentColor = Color.White
                )
            ) {
                Text("Modifica foto profilo", fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun HouseInfoCard(
    houseAddress: String,
    houseCode: String,
    isUpdatingCode: Boolean,
    codeUpdateError: String?,
    onUpdateCodeClick: (String) -> Unit,
    isUpdatingAddress: Boolean,
    addressUpdateError: String?,
    onUpdateAddressClick: (String) -> Unit,
    onLeaveHouseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF3B3054) else Color(0xFF6B53A4)
    val codeBoxBg = if (isDark) Color(0xFF2D2342) else Color(0xFFEBE5F7)
    val btnBgColorActual = if (isDark) Color(0xFF2D2342) else Color(0xFFEBE5F7)
    val btnTextColor = if (isDark) Color(0xFFFF6961) else Color(0xFFD32F2F)

    var editableAddress by remember(houseAddress) { mutableStateOf(houseAddress) }
    var editableCode by remember(houseCode) { mutableStateOf(houseCode) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(bgColor)
            .padding(24.dp)
    ) {
        Column {
            Text(
                text = "La tua casa",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))

            // 💅 GESTIONE DELL'INDIRIZZO ESATTAMENTE COME QUELLA DEL CODICE!
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    BasicTextField(
                        value = editableAddress,
                        onValueChange = { editableAddress = it.replace("\n", "").replace("\r", "") },
                        textStyle = TextStyle(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        cursorBrush = Brush.verticalGradient(listOf(Color.White, Color.White)),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            Box {
                                if (editableAddress.isEmpty()) {
                                    Text(
                                        text = houseAddress,
                                        color = Color.White.copy(alpha = 0.4f),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )

                    if (addressUpdateError != null) {
                        Text(
                            text = addressUpdateError,
                            color = Color(0xFFFF6961),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // 💅 SE L'INDIRIZZO E' STATO MODIFICATO COMPAIONO LA SPUNTA VERDE E LA X ROSSA
                if (editableAddress != houseAddress) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF6961).copy(alpha = 0.2f))
                                .clickable { editableAddress = houseAddress },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Annulla", tint = Color(0xFFFF6961), modifier = Modifier.size(20.dp))
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF34D399).copy(alpha = 0.2f))
                                .clickable {
                                    if (editableAddress.isNotBlank() && !isUpdatingAddress) {
                                        onUpdateAddressClick(editableAddress)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isUpdatingAddress) {
                                CircularProgressIndicator(color = Color(0xFF34D399), modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Check, contentDescription = "Salva", tint = Color(0xFF34D399), modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(codeBoxBg)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Il tuo codice:",
                        color = if (isDark) Color.LightGray else Color.DarkGray,
                        fontSize = 14.sp
                    )

                    BasicTextField(
                        value = editableCode,
                        onValueChange = { editableCode = it.replace("\n", "").replace("\r", "").uppercase() },
                        textStyle = TextStyle(
                            color = if (isDark) Color.White else Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        ),
                        cursorBrush = Brush.verticalGradient(
                            listOf(
                                if (isDark) Color.White else Color.Black,
                                if (isDark) Color.White else Color.Black
                            )
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            Box {
                                if (editableCode.isEmpty()) {
                                    Text(
                                        text = houseCode,
                                        color = Color.Gray.copy(alpha = 0.5f),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )

                    if (codeUpdateError != null) {
                        Text(
                            text = codeUpdateError,
                            color = Color(0xFFFF6961),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                if (editableCode != houseCode) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF6961).copy(alpha = 0.2f))
                                .clickable { editableCode = houseCode },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Annulla", tint = Color(0xFFFF6961), modifier = Modifier.size(20.dp))
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF34D399).copy(alpha = 0.2f))
                                .clickable {
                                    if (editableCode.isNotBlank() && !isUpdatingCode) {
                                        onUpdateCodeClick(editableCode)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isUpdatingCode) {
                                CircularProgressIndicator(color = Color(0xFF34D399), modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Check, contentDescription = "Salva", tint = Color(0xFF34D399), modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color.DarkGray else Color.White)
                            .clickable {
                                clipboardManager.setText(AnnotatedString(editableCode))
                                Toast.makeText(context, "Codice copiato!", Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copia codice",
                            tint = if (isDark) Color.White else Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLeaveHouseClick,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = btnBgColorActual,
                    contentColor = btnTextColor
                )
            ) {
                Text("Esci da questa casa", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun LogoutButtonPremium(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val btnBgColor = if (isDark) Color(0xFF4A1C1C) else Color(0xFFFFE5E5)
    val btnTextColor = if (isDark) Color(0xFFFF6961) else Color(0xFFD32F2F)

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = btnBgColor,
            contentColor = btnTextColor
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Disconnetti",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Disconnetti", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun RoommateRow(
    roommate: RoommateItem,
    isCurrentUserAdmin: Boolean,
    onPromoteClick: () -> Unit,
    onKickClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val contentColor = if (isDark) Color.White else Color.Black

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProfileAvatar(size = 48)

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${roommate.name} ${roommate.surname}".trim() + if (roommate.isMe) " (Tu)" else "",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = contentColor
            )
            Spacer(modifier = Modifier.height(4.dp))

            if (roommate.isAdmin) {
                Box(modifier = Modifier.background(Color(0xFFFFD700).copy(alpha = 0.2f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                    Text("Admin", color = Color(0xFFB8860B), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            } else {
                Box(modifier = Modifier.background(Color(0xFF6B53A4).copy(alpha = 0.1f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 2.dp)) {
                    Text("Coinquilino", color = Color(0xFF6B53A4), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        if (isCurrentUserAdmin && !roommate.isMe) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (!roommate.isAdmin) {
                    IconButton(
                        onClick = onPromoteClick,
                        modifier = Modifier.size(36.dp).background(Color(0xFFFFD700).copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.Default.WorkspacePremium, contentDescription = "Promuovi", tint = Color(0xFFB8860B), modifier = Modifier.size(20.dp))
                    }
                }

                IconButton(
                    onClick = onKickClick,
                    modifier = Modifier.size(36.dp).background(Color(0xFFFF6961).copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Caccia", tint = Color(0xFFFF6961), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val contentColor = if (isDark) Color.White else Color.Black
    val iconBg = if (isDark) Color(0xFF2D2342) else Color(0xFFEBE5F7)
    val iconTint = if (isDark) Color(0xFFB388FF) else Color(0xFF6B53A4)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            color = contentColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(16.dp)
        )
    }
}