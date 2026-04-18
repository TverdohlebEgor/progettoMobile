package com.example.progettomobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.progettomobile.ui.theme.ProgettoMobileTheme
import com.example.progettomobile.feature.PaginaIniziale

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.progettomobile.components.CustomBackButton
import com.example.progettomobile.feature.PaginaAnnunci
import com.example.progettomobile.feature.auth.PaginaLogin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProgettoMobileTheme {
                val navController = rememberNavController()
                val isDark = isSystemInDarkTheme()
                val BgColor = if (isDark) Color.Black else Color.White
                val ContentColor = if (isDark) Color.White else Color.Black


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BgColor
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
                            // Inserimento di uno Scaffold locale per replicare la gestione del padding
                            // della status bar presente in PaginaAnnunci. Garantisce che l'allineamento
                            // del layout sia pixel-perfect indipendentemente dal punto di ingresso.
                            Scaffold(
                                containerColor = BgColor
                            ) { paddingValues ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = paddingValues.calculateTopPadding())
                                ) {
                                    PaginaLogin(
                                        onLoginClick = { /*da fare*/},
                                        onRegisterClick = { /*da fare*/ }
                                    )
                                }
                            }
                        }


                        composable("annunci") {
                            PaginaAnnunci(onLoginClick = { /*da fare*/},
                                onRegisterClick = { /*da fare*/ },
                                onAnnuncioClick = { id -> navController.navigate("annuncio_singolo/$id")})
                        }
                    }

                }
            }
        }
    }
}