package cohappy.frontend.view.ad

import android.graphics.BitmapFactory
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import cohappy.frontend.components.HouseSetupSection
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