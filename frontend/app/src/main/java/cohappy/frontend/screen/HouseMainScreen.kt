package cohappy.frontend.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cohappy.frontend.components.FloatingBottomBar
import cohappy.frontend.components.NavItem
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze


@Composable
fun HouseMainScreen(
//    onLoginClick: (String, String) -> Unit,
//    onRegisterClick: () -> Unit,
//    onAnnuncioClick: (String) -> Unit,
//    onProfiloAnnunciClick: () -> Unit,
//    showError: Boolean,
//    isLoggedIn: Boolean,
//    onLogoutClick: () -> Unit = {},
//    onCreateHouseClick :() -> Unit,
//    onJoinConfirmClick: (String) -> Unit,
    onChatClick: (String) -> Unit,
    userToken: String? = null
) {
    var activeTab by remember { mutableStateOf("home") }
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
                "home" -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        HouseDashboardScreen(
                            userToken = userToken ?: "",
                            onChoreClick = { chore -> /**capire come collegare qui*/},
                            onWalletClick = { activeTab = "wallet" }
                        )

                    }
                }

                "chat" ->{
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)) {
                        ChatListScreen(
                            onChatClick = onChatClick,
                            userToken = userToken ?: ""
                        )
                    }
                }

                "wallet" ->{
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)) {
                        PortfolioScreen(
                            userToken = userToken ?: ""
                        )
                    }
                }

                "chore" ->{
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)) {
                        ChoresScreen(
                            userToken = userToken ?: "",
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuGestionaleUtente(currentTab: String, onTabSelected: (String) -> Unit){
    NavItem("home", Icons.Default.Home, "Home", currentTab, onTabSelected)
    NavItem("chat", Icons.Default.ChatBubble, "Chat", currentTab, onTabSelected)
    NavItem("wallet", Icons.Default.Wallet, "Wallet", currentTab, onTabSelected)
    NavItem("chore", Icons.Default.WaterDrop, "Annunci", currentTab, onTabSelected)
    NavItem("profilo", Icons.Default.Person, "Profilo", currentTab, onTabSelected)
}