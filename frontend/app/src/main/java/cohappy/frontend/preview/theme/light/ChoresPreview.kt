package cohappy.frontend.preview.theme.light

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.model.Chore
import cohappy.frontend.ui.theme.ProgettoMobileTheme
import cohappy.frontend.view.house.ChoresView

@Preview(showBackground = true, name = "1. Loading State")
@Composable
fun ChoresPreviewLoading() {
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

@Preview(showBackground = true, name = "2. Empty State")
@Composable
fun ChoresPreviewEmpty() {
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

@Preview(showBackground = true, name = "3. Populated State")
@Composable
fun ChoresPreviewPopulated() {
    val mockChores = listOf(
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

    ProgettoMobileTheme {
        ChoresView(
            nomeUtente = "Ale",
            imageBytes = null,
            isLoading = false,
            userToken = "my_token",
            chores = mockChores,
            onChoreToggle = { _, _, _ -> },
            onAddClick = {}
        )
    }
}