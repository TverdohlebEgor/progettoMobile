package cohappy.frontend.feature.auth

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cohappy.frontend.components.LoginRegisterButtonForLogin
import cohappy.frontend.components.LoginRegistration
import cohappy.frontend.components.Titoli

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.model.dto.request.LoginDTO
@Composable
fun PaginaLogin(onLoginClick: (String, String) -> Unit, onRegisterClick: () -> Unit,showError: Boolean,) {
    val isDark = isSystemInDarkTheme()
    val BgColor = if (isDark) Color.Black else Color.White
    val ContentColor = if (isDark) Color.White else Color.Black

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BgColor,
        contentColor = ContentColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            // Intestazione della schermata.
            // L'utilizzo di titolo e sottotitolo migliora la gerarchia visiva.
            Titoli(
                titolo1 = "Login",
                sottotitolo = "Bentornato, inserisci le tue credenziali.",
                color = ContentColor
            )

            // Spacer flessibile per centrare verticalmente il form di login.
            Spacer(modifier = Modifier.weight(1f))

            // Contenitore per i campi di input e i pulsanti.
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LoginRegistration(
                    onEmailChange = { nuovaEmail -> email = nuovaEmail },
                    onPasswordChange = { nuovaPassword -> password = nuovaPassword },
                    email = email,
                    password = password,
                    showError = showError
                )

                Spacer(modifier = Modifier.height(32.dp))

                LoginRegisterButtonForLogin(
                    onLoginClick = { onLoginClick(email, password) },
                    onRegisterClick = onRegisterClick,
                )
            }

            // Spacer flessibile inferiore per mantenere il contenuto esattamente al centro.
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}