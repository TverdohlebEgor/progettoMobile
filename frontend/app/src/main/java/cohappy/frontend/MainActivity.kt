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
import cohappy.frontend.feature.auth.PaginaRegistrazione
import cohappy.frontend.model.dto.request.LoginDTO
import cohappy.frontend.model.dto.request.RegisterDTO
import kotlinx.coroutines.launch

import android.content.Context
import cohappy.frontend.feature.ProfiloNoCasa
import cohappy.frontend.feature.annunci.ChatAnnunci

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
                    NavHost(navController = navController, startDestination = partenza){
                        composable("iniziale"){
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
                            var showError by remember { mutableStateOf(false) }

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
                                            lifecycleScope.launch {
                                                try {
                                                    val pacchettoLogin = LoginDTO(email = email, password = password)
                                                    val apiResponse = ClientSingleton.userApi.login(pacchettoLogin)

                                                    // 💅 Controlliamo l'errore in modo pulito tramite l'oggetto Response
                                                    if (!apiResponse.isSuccessful) {
                                                        throw Exception("Credenziali errate o utente non trovato dal server")
                                                    }

                                                    // 💅 Prendiamo IL VERO CORPO della risposta, ovvero il token!
                                                    val risposta = apiResponse.body() ?: ""

                                                    println("Login effettuato con successo. Dati: $risposta")
                                                    with (sharedPref.edit()) {
                                                        putString("USER_TOKEN", risposta)
                                                        apply()
                                                    }

                                                    isLoggedIn = true
                                                    userToken = risposta

                                                    navController.navigate("annunci") {
                                                        popUpTo("iniziale") { inclusive = true }
                                                    }
                                                } catch (e: Exception) {
                                                    println("Ops! Login fallito: ${e.message}")
                                                    showError = true
                                                }
                                            }
                                        },
                                        onRegisterClick = {
                                            navController.navigate("registration") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        },
                                        showError = showError
                                    )
                                }
                            }
                        }


                        composable("annunci") {
                            var showError by remember { mutableStateOf(false) }

                            PaginaAnnunci(
                                onLoginClick = { email, password ->
                                    showError = false

                                    lifecycleScope.launch {
                                        try {
                                            val pacchettoLogin = LoginDTO(email = email, password = password)
                                            val apiResponse = ClientSingleton.userApi.login(pacchettoLogin)

                                            if (!apiResponse.isSuccessful) {
                                                throw Exception("Credenziali errate o utente non trovato dal server")
                                            }

                                            // 💅 Prendiamo il VERO token anche qui!
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
                                onChatAnnunciClick = { navController.navigate("chat") },
                                onLogoutClick = {
                                    with(sharedPref.edit()) {
                                        remove("USER_TOKEN")
                                        apply()
                                    }
                                    isLoggedIn = false
                                    userToken = null
                                    navController.navigate("iniziale") {
                                        popUpTo(0) // Pulisce la cronologia
                                    }
                                },
                                onCreateHouseClick = { /* TODO */ },
                                onJoinConfirmClick = { /* TODO */ },
                                onEditClick = { /* TODO */ },
                                userToken = userToken
                            )
                        }

                        composable("registration"){
                            var showError by remember { mutableStateOf(false) }
                            Scaffold(
                                containerColor = bgColor
                            ) { paddingValues ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = paddingValues.calculateTopPadding())
                                ) {
                                    PaginaRegistrazione(
                                        onRegisterClick = { nome, cognome, dataNascita, email, telefono, password ->
                                            showError = false

                                            lifecycleScope.launch {
                                                try {
                                                    val pacchettoRegistrazione = RegisterDTO(name = nome, surname = cognome, birthDate = dataNascita, email = email, phoneNumber = telefono, password = password)
                                                    val risposta = ClientSingleton.userApi.register(pacchettoRegistrazione).toString()

                                                    if (risposta.contains("409") || risposta.contains("422") || risposta.contains("duplicate key error")) {
                                                        throw Exception("Utente già presente nel database")}
                                                    navController.navigate("login")
                                                }
                                                catch (e: Exception) {
                                                    println("Registrazione fallita: ${e.message}")
                                                    showError = true
                                                }
                                            }
                                        },
                                        onLoginClick = {navController.navigate("login") {
                                            popUpTo("registration") { inclusive = true }
                                        } },
                                        showError = showError
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}