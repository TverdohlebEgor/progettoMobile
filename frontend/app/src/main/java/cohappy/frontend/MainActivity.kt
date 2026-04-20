package cohappy.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import cohappy.frontend.ui.theme.ProgettoMobileTheme
import cohappy.frontend.feature.PaginaIniziale

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import cohappy.frontend.components.CustomBackButton
import cohappy.frontend.feature.annunci.PaginaAnnunci
import cohappy.frontend.feature.auth.PaginaLogin
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.model.dto.request.LoginDTO
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProgettoMobileTheme {
                val navController = rememberNavController()
                val isDark = isSystemInDarkTheme()
                val bgColor = if (isDark) Color.Black else Color.White
                val contentColor = if (isDark) Color.White else Color.Black


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = bgColor
                ) {
                    NavHost(navController = navController, startDestination = "iniziale"){
                        composable("iniziale"){
                            PaginaIniziale(
                                onLoginClick = { navController.navigate("login") },
                                onRegisterClick = { navController.navigate("register") },
                                onGuestClick = { navController.navigate("annunci") }
                            )
                        }

                        composable("login") {
                            var showError by remember { mutableStateOf(false) }

                            // Inserimento di uno Scaffold locale per replicare la gestione del padding
                            // della status bar presente in PaginaAnnunci. Garantisce che l'allineamento
                            // del layout sia pixel-perfect indipendentemente dal punto di ingresso.
                            Scaffold(
                                containerColor = bgColor
                            ) { paddingValues ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = paddingValues.calculateTopPadding())
                                ) {
                                    PaginaLogin(
                                        onLoginClick = { email, password ->
                                            showError = false
                                            // Avviamo una coroutine (lavoro in background)
                                            lifecycleScope.launch {
                                                try {
                                                    // 1. Prepariamo il pacchetto
                                                    val pacchettoLogin = LoginDTO(email = email, password = password)

                                                    // 2. Facciamo la chiamata e la trasformiamo in stringa per leggerla
                                                    val risposta = ClientSingleton.userApi.login(pacchettoLogin).toString()

                                                    // Controlliamo se Egor ci ha mandato un errore 404 o 400
                                                    if (risposta.contains("404") || risposta.contains("400") || risposta.contains("not found") || risposta.contains("Exception")) {
                                                        // Lanciamo l'eccezione a mano per finire dritti nel catch!
                                                        throw Exception("Credenziali errate o utente non trovato dal server")
                                                    }

                                                    // 3. SE ARRIVIAMO QUI, SIAMO DENTRO DAVVERO!
                                                    println("Login effettuato con successo. Dati: $risposta")

                                                    // 4. Cambiamo rotta
                                                    navController.navigate("annunci")

                                                } catch (e: Exception) {
                                                    // 💅 ACCENDIAMO IL MESSAGGIO DI ERRORE!
                                                    println("Ops! Login fallito: ${e.message}")
                                                    showError = true
                                                }
                                            }
                                        },
                                        onRegisterClick = { /*da fare*/ },
                                        showError = showError
                                    )
                                }
                            }
                        }


                        composable("annunci") {
                            var showError by remember { mutableStateOf(false) }
                            showError = showError
                            PaginaAnnunci(
                                onLoginClick = { email, password ->
                                showError = false

                                // Avviamo una coroutine (lavoro in background)
                                lifecycleScope.launch {
                                    try {
                                        // 1. Prepariamo il pacchetto
                                        val pacchettoLogin = LoginDTO(email = email, password = password)

                                        // 2. Facciamo la chiamata e la trasformiamo in stringa per leggerla
                                        val risposta = ClientSingleton.userApi.login(pacchettoLogin).toString()

                                        // Controlliamo se Egor ci ha mandato un errore 404 o 400
                                        if (risposta.contains("404") || risposta.contains("400") || risposta.contains("not found") || risposta.contains("Exception")) {
                                            // Lanciamo l'eccezione a mano per finire dritti nel catch!
                                            throw Exception("Credenziali errate o utente non trovato dal server")
                                        }

                                        // 3. SE ARRIVIAMO QUI, SIAMO DENTRO DAVVERO!
                                        println("Login effettuato con successo. Dati: $risposta")

                                        // 4. Cambiamo rotta
                                        navController.navigate("annunci")

                                    } catch (e: Exception) {
                                        // 💅 ACCENDIAMO IL MESSAGGIO DI ERRORE!
                                        println("Ops! Login fallito: ${e.message}")
                                        showError = true
                                    }
                                }
                            },
                                showError=showError,
                                onRegisterClick = { /*da fare*/ },
                                onAnnuncioClick = { id -> navController.navigate("annuncio_singolo/$id")})
                        }
                    }

                }
            }
        }
    }
}