package cohappy.frontend.view.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cohappy.frontend.components.CustomButton
import cohappy.frontend.components.CustomTextButtom
import cohappy.frontend.components.CustomTextField
import cohappy.frontend.components.Titoli
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationView(
    onRegisterClick: (String, String, String, String, String, String) -> Unit,
    onLoginClick: () -> Unit,
    showBackendError: Boolean = false,
    nameError: Boolean = false,
    surnameError: Boolean = false,
    dateError: Boolean = false,
    emailError: Boolean = false,
    phoneError: Boolean = false,
    passwordError: Boolean = false
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

    var localError by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val eighteenYearsAgo = LocalDate.now().minusYears(18)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
                return utcTimeMillis <= eighteenYearsAgo
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year <= LocalDate.now().year - 18
            }
        }
    )

    LaunchedEffect(Unit) {
        name = ""; surname = ""; birthDate = ""; email = ""; phoneNumber = ""; password = ""
        localError = false
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        birthDate = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Annulla")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = BgColor, contentColor = ContentColor) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)) {
            Titoli(
                titolo1 = "Registrati",
                sottotitolo = "Benvenuto, crea il tuo profilo.",
                color = ContentColor
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (localError) {
                    Text(
                        text = "Compila tutti i campi per registrarti!",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }

                Registration(
                    name = name,
                    cognome = surname,
                    annoNascita = birthDate,
                    email = email,
                    telefono = phoneNumber,
                    password = password,
                    onNameChange = { name = it },
                    onCognomeChange = { surname = it },
                    onEmailChange = { email = it },
                    onTelefonoChange = { phoneNumber = it },
                    onPasswordChange = { password = it },
                    showBackendError = showBackendError,
                    nameError = nameError,
                    surnameError = surnameError,
                    dateError = dateError,
                    emailError = emailError,
                    phoneError = phoneError,
                    passwordError = passwordError,
                    onDateClick = { showDatePicker = true }
                )

                Spacer(modifier = Modifier.height(32.dp))

                RegisterButton(
                    onRegisterClick = {
                        if (listOf(
                                name,
                                surname,
                                birthDate,
                                email,
                                phoneNumber,
                                password
                            ).all { it.isNotBlank() }
                        ) {
                            localError = false
                            onRegisterClick(
                                name.trim(),
                                surname.trim(),
                                birthDate.trim(),
                                email.trim(),
                                phoneNumber.trim(),
                                password.trim()
                            )
                        } else {
                            localError = true
                        }
                    },
                    onLoginClick = onLoginClick,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun Registration(
    name: String,
    cognome: String,
    annoNascita: String,
    email: String,
    telefono: String,
    password: String,
    onNameChange: (String) -> Unit,
    onCognomeChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onTelefonoChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    showBackendError: Boolean,
    nameError: Boolean,
    surnameError: Boolean,
    dateError: Boolean,
    emailError: Boolean,
    phoneError: Boolean,
    passwordError: Boolean,
    modifier: Modifier = Modifier,
    customFontSize: Int = 18,
    onDateClick: () -> Unit = {}
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (showBackendError) {
            Text(
                text = "Email o telefono già registrati, vai al login",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }

        val fields = listOf(
            RegistrationField(name, onNameChange, "nome", nameError, "Il nome deve avere almeno 2 caratteri"),
            RegistrationField(cognome, onCognomeChange, "cognome", surnameError, "Il cognome deve avere almeno 2 caratteri"),
            RegistrationField(email, onEmailChange, "email", emailError, "Email non valida"),
            RegistrationField(telefono, onTelefonoChange, "telefono", phoneError, "Telefono non valido"),
            RegistrationField(password, onPasswordChange, "password", passwordError, "La password deve avere minimo 6 caratteri")
        )

        fields.take(2).forEach { RegistrationTextField(it, customFontSize) }
        DatePicker(dateError, customFontSize, onDateClick, annoNascita)
        fields.drop(2).forEach { RegistrationTextField(it, customFontSize) }
    }
}

private data class RegistrationField(
    val value: String,
    val onValueChange: (String) -> Unit,
    val placeholder: String,
    val isError: Boolean,
    val errorMessage: String
)

@Composable
private fun RegistrationTextField(field: RegistrationField, fontSize: Int) {
    val errorColor = MaterialTheme.colorScheme.error
    FieldWrapper(field.isError, field.errorMessage) {
        CustomTextField(
            value = field.value,
            onValueChange = field.onValueChange,
            placeholder = field.placeholder,
            customFontSize = fontSize,
            modifier = Modifier.then(
                if (field.isError) Modifier.border(1.5.dp, errorColor, RoundedCornerShape(16.dp))
                else Modifier
            )
        )
    }
}

@Composable
fun DatePicker(
    dateError: Boolean,
    customFontSize: Int = 18,
    onDateClick: () -> Unit = {},
    annoNascita: String
) {
    val errorColor = MaterialTheme.colorScheme.error
    FieldWrapper(dateError, "Data obbligatoria") {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp)
                )
                .then(
                    if (dateError) Modifier.border(1.5.dp, errorColor, RoundedCornerShape(16.dp))
                    else Modifier
                )
                .clickable { onDateClick() }
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (annoNascita.isEmpty()) "data di nascita" else annoNascita,
                    fontSize = customFontSize.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (annoNascita.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun FieldWrapper(
    isError: Boolean,
    errorMessage: String,
    content: @Composable () -> Unit
) {
    Column {
        content()
        if (isError) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun RegisterButton(onRegisterClick: () -> Unit, onLoginClick: () -> Unit) {
    CustomButton(text = "Registrati", onClick = onRegisterClick, isPrimary = true, shape = "large")
    CustomTextButtom(text = "Hai già un account? Accedi", onClick = onLoginClick)
}

@Preview(showBackground = true, name = "Registration - Standard")
@Composable
fun PreviewRegistrationView() {
    MaterialTheme {
        RegistrationView(
            onRegisterClick = { _, _, _, _, _, _ -> },
            onLoginClick = { },
            showBackendError = false
        )
    }
}

@Preview(showBackground = true, name = "Registration - Backend Error")
@Composable
fun PreviewRegistrationViewError() {
    MaterialTheme {
        RegistrationView(
            onRegisterClick = { _, _, _, _, _, _ -> },
            onLoginClick = { },
            showBackendError = true
        )
    }
}

@Preview(showBackground = true, name = "Registration - Validation Errors")
@Composable
fun PreviewRegistrationViewValidation() {
    MaterialTheme {
        RegistrationView(
            onRegisterClick = { _, _, _, _, _, _ -> },
            onLoginClick = { },
            nameError = true,
            surnameError = true,
            dateError = true,
            emailError = true,
            phoneError = true,
            passwordError = true
        )
    }
}