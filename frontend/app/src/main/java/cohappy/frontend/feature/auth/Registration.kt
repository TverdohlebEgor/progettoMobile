package cohappy.frontend.feature.auth

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cohappy.frontend.components.RegisterButton
import cohappy.frontend.components.Registration
import cohappy.frontend.components.Titoli
import java.util.Calendar

@Composable
fun PaginaRegistrazione(
    onRegisterClick: (String, String, String, String, String, String) -> Unit,
    onLoginClick: () -> Unit,
    showError: Boolean = false // 💅 Aggiunto un valore di default
) {
    val isDark = isSystemInDarkTheme()
    val BgColor = if (isDark) Color.Black else Color.White
    val ContentColor = if (isDark) Color.White else Color.Black

    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // 💅 Nuovo secchiello: Se un utente dimentica un campo, accendiamo l'errore locale!
    var localError by remember { mutableStateOf(false) }

    // Pulizia dei campi ogni volta che la schermata viene ricaricata
    LaunchedEffect(Unit) {
        name = ""
        surname = ""
        birthDate = ""
        email = ""
        phoneNumber = ""
        password = ""
        localError = false
    }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // 💅 Blindato in un remember per non ricaricarlo a ogni tasto premuto!
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                // Assicuriamoci che il formato sia corretto (MM-DD con gli zeri)
                val formatMonth = String.format("%02d", month + 1)
                val formatDay = String.format("%02d", dayOfMonth)
                birthDate = "$year-$formatMonth-$formatDay"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

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
                titolo1 = "Registrati",
                sottotitolo = "Benvenuto, crea il tuo profilo.",
                color = ContentColor
            )

            Spacer(modifier = Modifier.height(0.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 💅 Messaggio di errore se i campi sono vuoti
                if (localError) {
                    Text(
                        text = "Compila tutti i campi per registrarti!",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                }

                Registration(
                    onEmailChange = { email = it },
                    onPasswordChange = { password = it },
                    onTelefonoChange = { phoneNumber = it },
                    onAnnoChange = { birthDate = it },
                    onCognomeChange = { surname = it },
                    onNameChange = { name = it },
                    telefono = phoneNumber,
                    annoNascita = birthDate,
                    cognome = surname,
                    email = email,
                    password = password,
                    showError = showError && !localError, // Mostra l'errore di Egor solo se non ce ne sono di nostri
                    name = name,
                    onDateClick = { datePickerDialog.show() }
                )

                Spacer(modifier = Modifier.height(32.dp))

                RegisterButton(
                    onRegisterClick = {
                        // 💅 MAGIC: Togliamo TUTTI gli spazi prima e dopo il testo per non far sbroccare il Login!
                        val cleanName = name.trim()
                        val cleanSurname = surname.trim()
                        val cleanEmail = email.trim()
                        val cleanPhone = phoneNumber.trim()
                        val cleanPassword = password.trim()
                        val cleanDate = birthDate.trim()

                        // Controlliamo che NESSUN campo sia vuoto prima di chiamare il server
                        if (cleanName.isNotBlank() &&
                            cleanSurname.isNotBlank() &&
                            cleanEmail.isNotBlank() &&
                            cleanPassword.isNotBlank() &&
                            cleanDate.isNotBlank() &&
                            cleanPhone.isNotBlank()) {

                            localError = false // Tutto ok
                            onRegisterClick(cleanName, cleanSurname, cleanDate, cleanEmail, cleanPhone, cleanPassword)
                        } else {
                            localError = true // Fail! Accendiamo la scritta rossa locale
                        }
                    },
                    onLoginClick = onLoginClick,
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}