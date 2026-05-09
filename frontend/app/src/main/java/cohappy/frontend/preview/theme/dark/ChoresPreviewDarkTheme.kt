package cohappy.frontend.preview.theme.dark

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.model.Chore
import cohappy.frontend.ui.theme.ProgettoMobileTheme
import cohappy.frontend.view.house.ChoresView

private val mockChoresDark = listOf(
    Chore(
        choreCode = "001",
        title = "Pulizia bagno",
        description = "Sanitari e specchio",
        assignedToCode = "my_token",
        assigneeName = "Te",
        isCompleted = false,
        dayLabel = "Oggi"
    ),
    Chore(
        choreCode = "002",
        title = "Buttare spazzatura",
        description = "Plastica e Umido",
        assignedToCode = "altro_utente",
        assigneeName = "Marco",
        isCompleted = true,
        dayLabel = "Martedì"
    ),
    Chore(
        choreCode = "003",
        title = "Aspirapolvere salotto",
        description = "Spostare anche i tappeti",
        assignedToCode = "altro_utente_2",
        assigneeName = "Sofia",
        isCompleted = false,
        dayLabel = "Giovedì"
    )
)

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "1. Dark Loading State"
)
@Composable
fun ChoresDarkPreviewLoading() {
    ProgettoMobileTheme {
        ChoresView(
            nomeUtente = "Ale",
            imageBytes = null,
            isLoading = true,
            userToken = "my_token",
            chores = emptyList(),
            onChoreToggle = { _, _, _ -> },
            onAddClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "2. Dark Empty State"
)
@Composable
fun ChoresDarkPreviewEmpty() {
    ProgettoMobileTheme {
        ChoresView(
            nomeUtente = "Ale",
            imageBytes = null,
            isLoading = false,
            userToken = "my_token",
            chores = emptyList(),
            onChoreToggle = { _, _, _ -> },
            onAddClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "3. Dark Populated State"
)
@Composable
fun ChoresDarkPreviewPopulated() {
    ProgettoMobileTheme {
        ChoresView(
            nomeUtente = "Ale",
            imageBytes = null,
            isLoading = false,
            userToken = "my_token",
            chores = mockChoresDark,
            onChoreToggle = { _, _, _ -> },
            onAddClick = {}
        )
    }
}