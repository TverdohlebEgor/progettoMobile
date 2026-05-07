package cohappy.frontend.screen

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cohappy.frontend.components.FloatingBottomBar
import cohappy.frontend.components.NavItem
import cohappy.frontend.viewmodel.RommateProfileViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze


@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun HouseMainScreen(
    onChatClick: (String) -> Unit,
    onAnnuncioClick: (String) -> Unit,
    onCreateAdClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onLeaveHouseSuccess: () -> Unit,
    userToken: String? = null,
    sharedPref: SharedPreferences? = null
) {
    var activeTab by remember { mutableStateOf("home") }
    var currentHouseCode by remember { 
        mutableStateOf(sharedPref?.getString("HOUSE_CODE", "") ?: "") 
    }
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
                            houseCode = sharedPref?.getString("HOUSE_CODE", "") ?: "",
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

                "annunci" -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AdListScreen(
                            innerPadding = paddingValues,
                            onAdClick = onAnnuncioClick
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

                "profilo" ->{
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)) {
                        RommateProfileScreen(
                            userToken = userToken ?: "",
                            houseCode = currentHouseCode,
                            onLogoutClick = onLogoutClick,
                            onLeaveHouseSuccess = onLeaveHouseSuccess,
                            onRoommatesClick = {},
                            onCreateAdClick = onCreateAdClick,
                            viewModel = RommateProfileViewModel(),
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
    NavItem("chore", Icons.Default.WaterDrop, "Chore", currentTab, onTabSelected)
    NavItem("profilo", Icons.Default.Person, "Profilo", currentTab, onTabSelected)
}