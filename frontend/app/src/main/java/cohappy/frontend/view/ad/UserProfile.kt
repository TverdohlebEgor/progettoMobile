package cohappy.frontend.view.ad

import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cohappy.frontend.components.LogoutTextButton
import cohappy.frontend.components.ProfileHeaderCard
import cohappy.frontend.components.Titoli

@Composable
fun UserProfileView(
    userName: String,
    userSurname: String,
    imageBytes: ByteArray?,
    isLoading: Boolean,
    onEditClick: () -> Unit,
    onCreateHouseClick: () -> Unit,
    onJoinConfirmClick: (String) -> Unit,
    onLogoutClick: () -> Unit
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

                ProfileHeaderCard(
                    nome = userName,
                    cognome = userSurname,
                    profileBitmap = profileBitmap,
                    onEditClick = onEditClick
                )

                Spacer(modifier = Modifier.height(32.dp))

                HouseSetupSection(
                    onCreateHouseClick = onCreateHouseClick,
                    onJoinConfirmClick = onJoinConfirmClick
                )

                Spacer(modifier = Modifier.height(25.dp))

                LogoutTextButton(onClick = onLogoutClick)

                Spacer(modifier = Modifier.height(120.dp))
            }
        }
    }
}

@Composable
fun HouseSetupSection(
    onCreateHouseClick: () -> Unit,
    onJoinConfirmClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var houseCode by remember { mutableStateOf("") }
    val isDark = isSystemInDarkTheme()
    val containerColor = if (isDark) Color(0xFF4A3973) else Color(0xFF6B53A4) // Viola scuro

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3.A BOX 1: CREA UNA CASA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(containerColor)
                .padding(24.dp)
        ) {
            Button(
                onClick = onCreateHouseClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = containerColor
                )
            ) {
                Text("Crea una casa", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        // 3.B SCRITTA "OPPURE"
        Text(
            text = "oppure",
            color = Color.Gray,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            modifier = Modifier.padding(vertical = 5.dp)
        )

        // 3.C BOX 2: ACCEDI A UNA CASA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(containerColor)
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Accedi a una casa",
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = houseCode,
                        onValueChange = { houseCode = it.uppercase() },
                        textStyle = LocalTextStyle.current.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp),
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (houseCode.isEmpty()) {
                                    Text("Codice (es. COH-8X2P)", color = Color.Gray.copy(alpha = 0.7f), fontSize = 14.sp)
                                }
                                innerTextField()
                            }
                        }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onJoinConfirmClick(houseCode) },
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.2f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Conferma")
                    }
                }
            }
        }
    }
}