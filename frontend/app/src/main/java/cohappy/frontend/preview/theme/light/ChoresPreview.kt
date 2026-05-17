package cohappy.frontend.preview.theme.light

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.model.Chore
import cohappy.frontend.ui.theme.ProgettoMobileTheme
import cohappy.frontend.view.house.ChoresView
import java.time.LocalDate

@Preview(showBackground = true, name = "1. Loading State")
@Composable
fun ChoresPreviewLoading() {
    ProgettoMobileTheme {
        ChoresView(
            nomeUtente = "Ale",
            imageBytes = null,
            isLoading = true,
            userToken = "my_token",
            selectedDate = LocalDate.now(),
            onDateSelected = {},
            chores = emptyList(),
            onChoreToggle = { _, _, _ -> },
            onAddChoreConfirm = { _, _, _, _, _ -> },
            roommates = emptyList()
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
            selectedDate = LocalDate.now(),
            onDateSelected = {},
            chores = emptyList(),
            onChoreToggle = { _, _, _ -> },
            onAddChoreConfirm = { _, _, _, _, _ -> },
            roommates = emptyList()
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
            assignedToCode = null, // Esempio non assegnata
            assigneeName = null,
            isCompleted = false,
            dayLabel = "Martedì"
        ),
        Chore(
            choreCode = "003",
            title = "Aspirapolvere salotto",
            description = "Spostare anche i tappeti",
            assignedToCode = "altro_utente_2",
            assigneeName = "Sofia",
            isCompleted = true,
            dayLabel = "Giovedì"
        )
    )

    val mockRoommates = listOf(
        "user1" to "Sofia",
        "user2" to "Marco",
        "my_token" to "Te"
    )

    ProgettoMobileTheme {
        ChoresView(
            nomeUtente = "Ale",
            imageBytes = null,
            isLoading = false,
            userToken = "my_token",
            selectedDate = LocalDate.now(),
            onDateSelected = {},
            chores = mockChores,
            onChoreToggle = { _, _, _ -> },
            onAddChoreConfirm = { _, _, _, _, _ -> },
            roommates = mockRoommates,
            daysWithChores = listOf(
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                LocalDate.now().minusDays(1)
            )
        )
    }
}

@Preview(showBackground = true, name = "4. Monthly State")
@Composable
fun ChoresPreviewMonthly() {
    val mockChores = listOf(
        Chore(
            choreCode = "001",
            title = "Pulizia bagno",
            description = "Sanitari e specchio",
            assignedToCode = "my_token",
            assigneeName = "Te",
            isCompleted = false,
            dayLabel = "Oggi"
        )
    )

    ProgettoMobileTheme {
        ChoresView(
            nomeUtente = "Ale",
            imageBytes = null,
            isLoading = false,
            userToken = "my_token",
            selectedDate = LocalDate.now(),
            onDateSelected = {},
            chores = mockChores,
            onChoreToggle = { _, _, _ -> },
            onAddChoreConfirm = { _, _, _, _, _ -> },
            initialCalendarMode = cohappy.frontend.view.house.CalendarMode.MONTH,
            roommates = emptyList(),
            daysWithChores = listOf(
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10)
            )
        )
    }
}
