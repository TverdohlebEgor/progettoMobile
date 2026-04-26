package cohappy.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.LoginDTO
import cohappy.frontend.screen.LoginScreen
import cohappy.frontend.screen.RegistrationScreen
import cohappy.frontend.screen.SingleAdScreen
import cohappy.frontend.screen.SingleChatScreen
import cohappy.frontend.ui.theme.ProgettoMobileTheme
import cohappy.frontend.view.PaginaIniziale
import cohappy.frontend.view.Ad.PaginaAnnunci
import cohappy.frontend.view.gestionale.ControllerGestionale
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPref = getSharedPreferences("CohappyPrefs", MODE_PRIVATE)
        val tokenSalvato = sharedPref.getString("USER_TOKEN", null)
        val partenza = if (tokenSalvato != null) "annunci" else "iniziale"

        setContent {
            ProgettoMobileTheme {
                val navController = rememberNavController()
                val isDark = isSystemInDarkTheme()
                val bgColor = if (isDark) Color.Black else Color.White
                val contentColor = if (isDark) Color.White else Color.Black

                var isLoggedIn by remember { mutableStateOf(tokenSalvato != null) }
                var userToken by remember { mutableStateOf(tokenSalvato) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = bgColor
                ) {
                    NavHost(navController = navController, startDestination = partenza) {
                        composable("iniziale") {
                            PaginaIniziale(
                                onLoginClick = { navController.navigate("login") },
                                onRegisterClick = { navController.navigate("registration") },
                                onGuestClick = {
                                    navController.navigate("annunci")
                                    isLoggedIn = false
                                    userToken = null
                                }
                            )
                        }

                        composable("login") {
                            LoginScreen(
                                sharedPref = sharedPref,
                                onNavigateToAnnunci = { newToken ->
                                    isLoggedIn = true
                                    userToken = newToken
                                    navController.navigate("annunci") {
                                        popUpTo("iniziale") { inclusive = true }
                                    }
                                },
                                onNavigateToRegistration = {
                                    navController.navigate("registration") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("registration") {
                            RegistrationScreen(
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo("registration") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("annunci") {
                            var showError by remember { mutableStateOf(false) }

                            PaginaAnnunci(
                                onLoginClick = { email, password ->
                                    showError = false

                                    lifecycleScope.launch {
                                        try {
                                            val pacchettoLogin =
                                                LoginDTO(email = email, password = password)
                                            val apiResponse =
                                                ClientSingleton.userApi.login(pacchettoLogin)

                                            if (!apiResponse.isSuccessful) {
                                                throw Exception("Credenziali errate o utente non trovato dal server")
                                            }

                                            val risposta = apiResponse.body() ?: ""

                                            println("Login effettuato con successo. Dati: $risposta")
                                            with(sharedPref.edit()) {
                                                putString("USER_TOKEN", risposta)
                                                apply()
                                            }

                                            isLoggedIn = true
                                            userToken = risposta
                                        } catch (e: Exception) {
                                            println("Login fallito: ${e.message}")
                                            showError = true
                                        }
                                    }
                                },
                                showError = showError,
                                onRegisterClick = { navController.navigate("registration") },
                                onAnnuncioClick = { id -> navController.navigate("annuncio_singolo/$id") },
                                isLoggedIn = isLoggedIn,
                                onProfiloAnnunciClick = { navController.navigate("profilo_no_casa") },
                                onChatAnnunciClick = { chatId -> navController.navigate("chat_singola/$chatId") },
                                onLogoutClick = {
                                    with(sharedPref.edit()) {
                                        clear()
                                        apply()
                                    }
                                    isLoggedIn = false
                                    userToken = null

                                    navController.navigate("iniziale") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onCreateHouseClick = { navController.navigate("home_gestionale") },
                                onJoinConfirmClick = { /* TODO */ },
                                userToken = userToken
                            )
                        }

                        composable("chat_singola/{chatCode}") { backStackEntry ->
                            val chatCode =
                                backStackEntry.arguments?.getString("chatCode")?.trim() ?: ""

                            android.util.Log.d(
                                "TAG_CHECK_CHAT",
                                "🚀 NavHost riceve chatCode/hostCode: '$chatCode'"
                            )

                            if (chatCode.isNotBlank()) {
                                SingleChatScreen(
                                    chatCode = chatCode,
                                    userToken = userToken,
                                    onBackClick = { navController.popBackStack() },
                                    onNavigateToAnnuncio = { annuncioId ->
                                        navController.navigate("annuncio_singolo/$annuncioId")
                                    }
                                )
                            } else {
                                android.util.Log.e(
                                    "TAG_CHECK_CHAT",
                                    "❌ NavHost ERRORE CRITICO: chatCode è vuoto!"
                                )
                            }
                        }

                        composable(
                            route = "home_gestionale",
                        ) {
                            ControllerGestionale(userToken = userToken)
                        }

                        composable(
                            route = "annuncio_singolo/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val annuncioId = backStackEntry.arguments?.getString("id") ?: ""

                            SingleAdScreen(
                                annuncioId = annuncioId,
                                onBackClick = { navController.popBackStack() },
                                onChatClick = { targetChat ->
                                    navController.navigate("chat_singola/$targetChat")
                                },
                                onRequireLoginClick = {
                                    navController.navigate("login")
                                },
                                userToken = userToken
                            )
                        }
                    }
                }
            }
        }
    }
}