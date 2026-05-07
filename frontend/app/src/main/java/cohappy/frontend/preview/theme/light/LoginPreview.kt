package cohappy.frontend.preview.theme.light

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.view.auth.LoginView

@Preview(showBackground = true, name = "Login - Standard")
@Composable
fun PreviewPaginaLoginStandard() {
    LoginView(
        onLoginClick = { _, _ -> },
        onRegisterClick = { },
        showError = false
    )
}

@Preview(showBackground = true, name = "Login - Loading ")
@Composable
fun PreviewPaginaLoginLoading() {
    LoginView(
        onLoginClick = { _, _ -> },
        onRegisterClick = { },
        showError = false,
        isLoading = true
    )
}

@Preview(showBackground = true, name = "Login - With Unexpected Error")
@Composable
fun PreviewPaginaLoginWithUnexpectedError() {
    LoginView(
        onLoginClick = { _, _ -> },
        onRegisterClick = { },
        showError = true
    )
}

@Preview(showBackground = true, name = "Login - With NotFound Error")
@Composable
fun PreviewPaginaLoginWithNotFoundError() {
    LoginView(
        onLoginClick = { _, _ -> },
        onRegisterClick = { },
        showError = true,
        errorMessage = "Le credenziali fornite non sono corrette"
    )
}

@Preview(showBackground = true, name = "Login - With Server Error")
@Composable
fun PreviewPaginaLoginWithServerError() {
    LoginView(
        onLoginClick = { _, _ -> },
        onRegisterClick = { },
        showError = true,
        errorMessage = "Errore del server, ci scusiamo per il disagio"
    )
}