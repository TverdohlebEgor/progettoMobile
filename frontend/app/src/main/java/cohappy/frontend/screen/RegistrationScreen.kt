package cohappy.frontend.screen

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
import cohappy.frontend.model.RegistrationViewModel
import cohappy.frontend.model.RegistrationViewModelFactory
import cohappy.frontend.view.auth.RegistrationView

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel = viewModel(factory = RegistrationViewModelFactory()),
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color.Black else Color.White

    LaunchedEffect(uiState.isRegistrationSuccessful) {
        if (uiState.isRegistrationSuccessful) {
            onNavigateToLogin()
        }
    }

    Scaffold(containerColor = bgColor) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            RegistrationView(
                onRegisterClick = { nome, cognome, dataNascita, email, telefono, password ->
                    viewModel.register(nome, cognome, dataNascita, email, telefono, password)
                },
                onLoginClick = onNavigateToLogin,
                showBackendError = uiState.backendError,
                nameError = uiState.nameError,
                surnameError = uiState.surnameError,
                dateError = uiState.dateError,
                emailError = uiState.emailError,
                phoneError = uiState.phoneError,
                passwordError = uiState.passwordError
            )
        }
    }
}