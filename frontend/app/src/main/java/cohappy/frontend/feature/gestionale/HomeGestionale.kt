package cohappy.frontend.feature.gestionale

import cohappy.frontend.feature.annunci.ListaAnnunciView
import cohappy.frontend.feature.annunci.ProfiloNoCasa
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cohappy.frontend.components.FloatingBottomBar
import cohappy.frontend.components.MenuAnnunciLoggato
import cohappy.frontend.components.MenuGestionaleUtente
import cohappy.frontend.components.MenuOspite
import cohappy.frontend.feature.auth.PaginaLogin
import cohappy.frontend.feature.chat.ElencoChat
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze


@Composable
fun HomeGestionale(
//    onLoginClick: (String, String) -> Unit,
//    onRegisterClick: () -> Unit,
//    onAnnuncioClick: (String) -> Unit,
//    onProfiloAnnunciClick: () -> Unit,
//    onChatAnnunciClick: (String) -> Unit,
//    showError: Boolean,
//    isLoggedIn: Boolean,
//    onLogoutClick: () -> Unit = {},
//    onCreateHouseClick :() -> Unit,
//    onJoinConfirmClick: (String) -> Unit,
//    userToken: String? = null
) {
    var activeTab by remember { mutableStateOf("annunci") }
    val isDark = isSystemInDarkTheme()
    val BgColor = if (isDark) Color.Black else Color.White
    val hazeState = remember { HazeState() }

    Scaffold(
        bottomBar = {
            FloatingBottomBar(hazeState = hazeState) {
                MenuGestionaleUtente(
                    currentTab = activeTab,
                    onTabSelected = { nuovaTab -> activeTab = nuovaTab }
                )
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

//                        ListaAnnunciView(
//                            innerPadding = paddingValues,
//                            onAnnuncioClick = onAnnuncioClick
//                        )
                    }
                }
                }
            }
        }
    }
