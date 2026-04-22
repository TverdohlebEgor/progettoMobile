package cohappy.frontend.feature.annunci

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.components.FloatingBottomBar
import cohappy.frontend.components.MenuAnnunciLoggato
import cohappy.frontend.components.MenuOspite
import cohappy.frontend.feature.ProfiloNoCasa
import cohappy.frontend.feature.auth.PaginaLogin
import cohappy.frontend.feature.chat.ElencoChat
import cohappy.frontend.model.dto.HouseStateEnum
import cohappy.frontend.model.dto.request.CreateHouseAdvertisementDTO
import cohappy.frontend.model.dto.request.CreateHouseDTO
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.io.encoding.Base64


@Composable
fun PaginaAnnunci(
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit,
    onAnnuncioClick: (String) -> Unit,
    onProfiloAnnunciClick: () -> Unit,
    onChatAnnunciClick: (String) -> Unit,
    showError: Boolean,
    isLoggedIn: Boolean,
    onLogoutClick: () -> Unit = {},
    onCreateHouseClick :() -> Unit,
    onJoinConfirmClick: (String) -> Unit,
    onEditClick: () -> Unit,
    userToken: String? = null
) {
    var activeTab by remember { mutableStateOf("annunci") }
    val isDark = isSystemInDarkTheme()
    val BgColor = if (isDark) Color.Black else Color.White
    val hazeState = remember { HazeState() }

    Scaffold(
        bottomBar = {
            FloatingBottomBar(hazeState = hazeState) {
                if(isLoggedIn){
                    MenuAnnunciLoggato(
                        currentTab = activeTab,
                        onTabSelected = { nuovaTab -> activeTab = nuovaTab }
                    )
                } else {
                    MenuOspite(
                        currentTab = activeTab,
                        onTabSelected = { nuovaTab -> activeTab = nuovaTab }
                    )
                }
            }
        },
        containerColor = BgColor
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .haze(state = hazeState)
                .padding(top = paddingValues.calculateTopPadding())
                .background(BgColor)
        ){
            when (activeTab) {
                "annunci" -> {
                    Box(modifier = Modifier.fillMaxSize()) {

                        ListaAnnunciView(
                            innerPadding = paddingValues,
                            onAnnuncioClick = onAnnuncioClick
                        )
                    }
                }


                "chat" -> ElencoChat(
                    userToken = userToken,
                    onChatClick = onChatAnnunciClick
                )

                "profilo" -> {
                    if(isLoggedIn){
                        ProfiloNoCasa(
                            onLogoutClick = onLogoutClick,
                            onCreateHouseClick = onCreateHouseClick,
                            onJoinConfirmClick = onJoinConfirmClick,
                            onEditClick = onEditClick,
                            userToken = userToken ?: ""
                        )
                    } else {
                        PaginaLogin(
                            onLoginClick = onLoginClick,
                            onRegisterClick = onRegisterClick,
                            showError = showError
                        )
                    }
                }
            }
        }
    }
}