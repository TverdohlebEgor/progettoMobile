package cohappy.frontend.preview.theme.dark

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.model.Chore
import cohappy.frontend.ui.theme.ProgettoMobileTheme
import cohappy.frontend.view.house.ChoresView
import java.time.LocalDate

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

private val mockRoommatesDark = listOf(
    "user1" to "Sofia",
    "user2" to "Marco",
    "my_token" to "Te"
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
            selectedDate = LocalDate.now(),
            onDateSelected = {},
            chores = emptyList(),
            onChoreToggle = { _, _, _ -> },
            onAddChoreConfirm = { _, _, _, _, _ -> },
            roommates = emptyList()
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
            selectedDate = LocalDate.now(),
            onDateSelected = {},
            chores = emptyList(),
            onChoreToggle = { _, _, _ -> },
            onAddChoreConfirm = { _, _, _, _, _ -> },
            roommates = mockRoommatesDark
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
            selectedDate = LocalDate.now(),
            onDateSelected = {},
            chores = mockChoresDark,
            onChoreToggle = { _, _, _ -> },
            onAddChoreConfirm = { _, _, _, _, _ -> },
            roommates = mockRoommatesDark,
            daysWithChores = listOf(
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                LocalDate.now().minusDays(1)
            )
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "4. Dark Monthly State"
)
@Composable
fun ChoresDarkPreviewMonthly() {
    ProgettoMobileTheme {
        ChoresView(
            nomeUtente = "Ale",
            imageBytes = null,
            isLoading = false,
            userToken = "my_token",
            selectedDate = LocalDate.now(),
            onDateSelected = {},
            chores = mockChoresDark,
            onChoreToggle = { _, _, _ -> },
            onAddChoreConfirm = { _, _, _, _, _ -> },
            initialCalendarMode = cohappy.frontend.view.house.CalendarMode.MONTH,
            roommates = mockRoommatesDark,
            daysWithChores = listOf(
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(10)
            )
        )
    }
}
