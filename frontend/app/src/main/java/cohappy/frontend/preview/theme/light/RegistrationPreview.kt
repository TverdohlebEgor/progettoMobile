package cohappy.frontend.preview.theme.light

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.expections.ErrorMessages.ALREADY_USED_CREDENTIAL_REGISTRATION
import cohappy.frontend.expections.ErrorMessages.SERVER_ERROR
import cohappy.frontend.view.auth.RegistrationView

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

@Preview(showBackground = true, name = "Registration - Loading")
@Composable
fun PreviewRegistrationViewLoading() {
    MaterialTheme {
        RegistrationView(
            onRegisterClick = { _, _, _, _, _, _ -> },
            onLoginClick = { },
            showBackendError = false,
            isLoading = true
        )
    }
}

@Preview(showBackground = true, name = "Registration - Unexpected Error")
@Composable
fun PreviewRegistrationViewUnexpectedError() {
    MaterialTheme {
        RegistrationView(
            onRegisterClick = { _, _, _, _, _, _ -> },
            onLoginClick = { },
            showBackendError = true
        )
    }
}

@Preview(showBackground = true, name = "Registration - Already Registered Error")
@Composable
fun PreviewRegistrationViewAlreadyRegisteredError() {
    MaterialTheme {
        RegistrationView(
            onRegisterClick = { _, _, _, _, _, _ -> },
            onLoginClick = { },
            showBackendError = true,
            errorMessage = ALREADY_USED_CREDENTIAL_REGISTRATION
        )
    }
}

@Preview(showBackground = true, name = "Registration - Already Server Error")
@Composable
fun PreviewRegistrationViewAlreadyServerError() {
    MaterialTheme {
        RegistrationView(
            onRegisterClick = { _, _, _, _, _, _ -> },
            onLoginClick = { },
            showBackendError = true,
            errorMessage = SERVER_ERROR
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