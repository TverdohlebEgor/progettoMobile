package cohappy.frontend.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cohappy.frontend.R
import cohappy.frontend.components.LoginActionButtons
import cohappy.frontend.components.WelcomeHeaderImage

// Schermata di login dell'applicazione
@Composable
fun PaginaIniziale(onGuestClick: () -> Unit, onLoginClick: () -> Unit, onRegisterClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WelcomeHeaderImage(
            title = "Cohappy ✨",
            subtitle = "La tua casa, i tuoi coinquilini, zero stress.",
            imageRes = R.drawable.casa1

        )

        Spacer(modifier = Modifier.weight(1f))

        // I bottoni, con padding laterale
        LoginActionButtons(
            onLoginClick = onLoginClick,
            onRegisterClick = onRegisterClick,
            onGuestClick = onGuestClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        )

    }
}