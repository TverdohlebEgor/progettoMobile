package cohappy.frontend.screen
import android.content.SharedPreferences
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.model.LoginViewModel
import cohappy.frontend.model.LoginViewModelFactory
import cohappy.frontend.view.auth.LoginView

@Composable
fun LoginScreen(
    sharedPref: SharedPreferences,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(sharedPref)),
    onNavigateToAnnunci: (String) -> Unit,
    onNavigateToRegistration: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color.Black else Color.White

    LaunchedEffect(uiState.isLoginSuccessful) {
        if (uiState.isLoginSuccessful && uiState.token != null) {
            onNavigateToAnnunci(uiState.token!!)
        }
    }

    Scaffold(containerColor = bgColor) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            LoginView(
                onLoginClick = { email, password ->
                    viewModel.login(email, password)
                },
                onRegisterClick = onNavigateToRegistration,
                showError = uiState.showError
            )
        }
    }
}