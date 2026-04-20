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
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cohappy.frontend.components.RegisterButton
import cohappy.frontend.components.Registration
import cohappy.frontend.components.Titoli
import java.util.Calendar

@Composable
fun PaginaRegistrazione(
    onRegisterClick: (String, String, String, String, String, String) -> Unit,
    onLoginClick: () -> Unit,
    showError: Boolean
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

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            birthDate = "$year-${month + 1}-$dayOfMonth"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

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
                titolo1 = "Registati",
                sottotitolo = "Benvenuto, crea il tuo profilo.",
                color = ContentColor
            )

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
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
                    showError = showError,
                    name = name,
                    onDateClick = { datePickerDialog.show() }
                )

                Spacer(modifier = Modifier.height(32.dp))

                RegisterButton(
                    onRegisterClick = {
                        onRegisterClick(name, surname, birthDate, email, phoneNumber, password)
                    },
                    onLoginClick = onLoginClick,
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}