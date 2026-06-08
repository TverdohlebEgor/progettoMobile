package cohappy.frontend.screen

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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cohappy.frontend.components.FloatingBottomBar
import cohappy.frontend.components.NavItem
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze


@Composable
fun AdsMainScreen(
    sharedPref: SharedPreferences,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onAnnuncioClick: (String) -> Unit,
    onChatAnnunciClick: (String) -> Unit,
    isLoggedIn: Boolean,
    onLogoutClick: () -> Unit = {},
    onCreateHouseClick :() -> Unit,
    onJoinConfirmClick: (String) -> Unit,
    onHouseDetected: (String) -> Unit = {},
    userToken: String? = null
) {
    var activeTab by remember { mutableStateOf("annunci") }
    val isDark = isSystemInDarkTheme()
    val BgColor = if (isDark) Color.Black else Color.White
    val hazeState = remember { HazeState() }

    // Controllo automatico se l'utente è già in una casa ma il codice non è salvato in locale
    LaunchedEffect(isLoggedIn, userToken) {
        if (isLoggedIn && userToken != null && sharedPref.getString("HOUSE_CODE", null) == null) {
            try {
                val response = cohappy.frontend.client.ClientSingleton.userApi.getUserProfile(userToken)
                if (response.isSuccessful) {
                    val houseCode = response.body()?.houseCode
                    if (houseCode != null) {
                        onHouseDetected(houseCode)
                    }
                }
            } catch (e: Exception) {
                // Silently fail or handle error
            }
        }
    }

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

                        AdListScreen(
                            onAdClick = onAnnuncioClick
                        )
                    }
                }

                "chat" -> {
                    ChatListScreen(
                        userToken = userToken,
                        onChatClick = onChatAnnunciClick
                    )
                }

                "profilo" -> {
                    if(isLoggedIn){
                        UserProfileScreen(
                            onLogoutClick = onLogoutClick,
                            onCreateHouseClick = onCreateHouseClick,
                            onJoinConfirmClick = onJoinConfirmClick,
                            userToken = userToken ?: "",


                        )
                    } else {
                        LoginScreen(
                            sharedPref = sharedPref,
                            onNavigateToAnnunci = { onLoginSuccess() },
                            onNavigateToRegistration = onRegisterClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuOspite(currentTab: String, onTabSelected: (String) -> Unit) {
    NavItem("annunci", Icons.Default.Home, "Annunci", currentTab, onTabSelected)
    NavItem("profilo", Icons.Default.Person, "Profilo", currentTab, onTabSelected)
}

@Composable
fun MenuAnnunciLoggato(currentTab: String, onTabSelected: (String) -> Unit) {
    NavItem("annunci", Icons.Default.Home, "Annunci", currentTab, onTabSelected)
    NavItem("chat", Icons.Default.ChatBubble ,"Chat", currentTab, onTabSelected)
    NavItem("profilo", Icons.Default.Person, "Profilo", currentTab, onTabSelected)
}