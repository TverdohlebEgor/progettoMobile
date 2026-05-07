package cohappy.frontend.view.auth

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cohappy.frontend.components.CustomButton
import cohappy.frontend.components.CustomTextButtom
import cohappy.frontend.components.CustomTextField
import cohappy.frontend.components.Titoli

@Composable
fun LoginView(
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit,
    showError: Boolean,
    errorMessage: String? = null,
    isLoading: Boolean = false
) {
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
            Titoli(
                titolo1 = "Login",
                sottotitolo = "Bentornato, inserisci le tue credenziali.",
                color = ContentColor
            )

            Spacer(modifier = Modifier.height(80.dp))

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
                    errorMessage = errorMessage,
                    showError = showError
                )

                Spacer(modifier = Modifier.height(32.dp))

                LoginRegisterButtonForLogin(
                    onLoginClick = { onLoginClick(email, password) },
                    onRegisterClick = onRegisterClick,
                    isLoading = isLoading
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun LoginRegisterButtonForLogin(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    isLoading: Boolean = false
) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Accesso in corso ..."
        )
    } else {
        CustomButton(
            text = "Accedi",
            onClick = onLoginClick,
            isPrimary = true,
            shape = "large"
        )

        CustomTextButtom(
            text = "Non hai ancora un account? Registrati",
            onClick = onRegisterClick,
        )
    }
}


@Composable
fun LoginRegistration(
    showError: Boolean,
    errorMessage: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    customFontSize: Int = 18,
    email: String,
    password: String
) {
    Column(modifier = modifier) {
        if (showError) {
            Text(
                text = errorMessage ?: "Si è verificato un errore interno, ci scusiamo per il disagio",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
        } else {
            Text(
                text = "",
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = "email o telefono",
            customFontSize = customFontSize
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = "password",
            customFontSize = customFontSize
        )
    }
}
