package cohappy.frontend.preview.theme.dark

import android.content.res.Configuration
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.expections.ErrorMessages.ALREADY_USED_CREDENTIAL_REGISTRATION
import cohappy.frontend.expections.ErrorMessages.SERVER_ERROR
import cohappy.frontend.view.auth.RegistrationView

@Preview(showBackground = true, name = "Registration - Standard", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewRegistrationViewDarkTheme() {
    MaterialTheme {
        RegistrationView(
            onRegisterClick = { _, _, _, _, _, _ -> },
            onLoginClick = { },
            showBackendError = false
        )
    }
}

@Preview(showBackground = true, name = "Registration - Loading", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewRegistrationViewLoadingDarkTheme() {
    MaterialTheme {
        RegistrationView(
            onRegisterClick = { _, _, _, _, _, _ -> },
            onLoginClick = { },
            showBackendError = false,
            isLoading = true
        )
    }
}

@Preview(showBackground = true, name = "Registration - Unexpected Error", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewRegistrationViewUnexpectedErrorDarkTheme() {
    MaterialTheme {
        RegistrationView(
            onRegisterClick = { _, _, _, _, _, _ -> },
            onLoginClick = { },
            showBackendError = true
        )
    }
}

@Preview(showBackground = true, name = "Registration - Already Registered Error", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewRegistrationViewAlreadyRegisteredErrorDarkTheme() {
    MaterialTheme {
        RegistrationView(
            onRegisterClick = { _, _, _, _, _, _ -> },
            onLoginClick = { },
            showBackendError = true,
            errorMessage = ALREADY_USED_CREDENTIAL_REGISTRATION
        )
    }
}

@Preview(showBackground = true, name = "Registration - Already Server Error", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewRegistrationViewAlreadyServerErrorDarkTheme() {
    MaterialTheme {
        RegistrationView(
            onRegisterClick = { _, _, _, _, _, _ -> },
            onLoginClick = { },
            showBackendError = true,
            errorMessage = SERVER_ERROR
        )
    }
}

@Preview(showBackground = true, name = "Registration - Validation Errors", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewRegistrationViewValidationDarkTheme() {
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