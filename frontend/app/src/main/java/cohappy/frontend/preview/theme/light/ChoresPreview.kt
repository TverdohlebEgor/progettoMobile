package cohappy.frontend.preview.theme.light

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.model.Chore
import cohappy.frontend.view.house.ChoresView

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewChores() {
    val fakeChores = listOf(
        Chore(
            choreCode = "CHORE_1",
            title = "Pulizia Bagno",
            description = "Sanitari e pavimenti",
            assignedToCode = "test-token",
            assigneeName = "Tu",
            isCompleted = false,
            dayLabel = "Oggi"
        ),
        Chore(
            choreCode = "CHORE_2",
            title = "Spazzatura",
            description = "Svuotare tutti i cestini",
            assignedToCode = "MARCO_123",
            assigneeName = "Marco",
            isCompleted = false,
            dayLabel = "Oggi"
        ),
        Chore(
            choreCode = "CHORE_3",
            title = "Cucina",
            description = "Pulire i fornelli",
            assignedToCode = "test-token",
            assigneeName = "Tu",
            isCompleted = true,
            dayLabel = "Ieri"
        ),
        Chore(
            choreCode = "CHORE_4",
            title = "Lavastoviglie",
            description = "Svuotare e caricare",
            assignedToCode = "SOFIA_456",
            assigneeName = "Sofia",
            isCompleted = false,
            dayLabel = "Domani"
        )
    )

    MaterialTheme {
        ChoresView(
            nomeUtente = "Ale",
            imageBytes = null,
            isLoading = false,
            userToken = "test-token",
            chores = fakeChores
        )
    }
}