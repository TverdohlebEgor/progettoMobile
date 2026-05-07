package cohappy.frontend.preview.theme.dark

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.view.auth.LoginView

@Preview(showBackground = true, name = "Login - Standard Error DarkTheme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewPaginaLoginStandardDarkTheme() {
    LoginView(
        onLoginClick = { _, _ -> },
        onRegisterClick = { },
        showError = false
    )
}

@Preview(showBackground = true, name = "Login - Loading DarkTheme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewPaginaLoginLoadingDarkTheme() {
    LoginView(
        onLoginClick = { _, _ -> },
        onRegisterClick = { },
        showError = false,
        isLoading = true
    )
}

@Preview(showBackground = true, name = "Login - With Unexpected Error DarkTheme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewPaginaLoginWithUnexpectedErrorDarkTheme() {
    LoginView(
        onLoginClick = { _, _ -> },
        onRegisterClick = { },
        showError = true
    )
}

@Preview(showBackground = true, name = "Login - With NotFound Error DarkTheme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewPaginaLoginWithNotFoundErrorDarkTheme() {
    LoginView(
        onLoginClick = { _, _ -> },
        onRegisterClick = { },
        showError = true,
        errorMessage = "Le credenziali fornite non sono corrette"
    )
}

@Preview(showBackground = true, name = "Login - With Server Error DarkTheme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewPaginaLoginWithServerErrorDarkTheme() {
    LoginView(
        onLoginClick = { _, _ -> },
        onRegisterClick = { },
        showError = true,
        errorMessage = "Errore del server, ci scusiamo per il disagio"
    )
}