package cohappy.frontend.preview.theme.light

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.client.dto.response.GetNotificationDTO
import cohappy.frontend.ui.theme.ProgettoMobileTheme
import cohappy.frontend.view.house.HouseDashboardView

private val mockNotifications = listOf(
    GetNotificationDTO(
        eventId = "1",
        eventType = "CHAT",
        title = "Nuovo messaggio da Anna",
        subtitle = "Certo, a che ora ci vediamo?",
        timestamp = "10:30",
        imageBytes = null,
        userCode = "my_token"
    ),
    GetNotificationDTO(
        eventId = "2",
        eventType = "CHORE",
        title = "Promemoria Faccende",
        subtitle = "Ricordati di buttare l'umido oggi!",
        timestamp = "09:00",
        imageBytes = null,
        userCode = "my_token"
    ),
    GetNotificationDTO(
        eventId = "3",
        eventType = "PORTFOLIO",
        title = "Nuova Spesa Aggiunta",
        subtitle = "Marco ha aggiunto: Spesa Conad",
        timestamp = "Ieri",
        imageBytes = null,
        userCode = "my_token"
    )
)

@Preview(showBackground = true, name = "1. Loading State")
@Composable
fun HouseDashboardPreviewLoading() {
    ProgettoMobileTheme {
        HouseDashboardView(
            nomeUtente = "Ale",
            imageBytes = null,
            isLoading = true,
            userToken = "my_token",
            houseAddress = "Via Garibaldi 12",
            onAddClick = {},
            notifications = emptyList(),
            nextChoreName = "...",
            nextChoreDeadline = "...",
            totalDebtAmount = "..."
        )
    }
}

@Preview(showBackground = true, name = "2. Empty State")
@Composable
fun HouseDashboardPreviewEmpty() {
    ProgettoMobileTheme {
        HouseDashboardView(
            nomeUtente = "Ale",
            imageBytes = null,
            isLoading = false,
            userToken = "my_token",
            houseAddress = "Via Garibaldi 12",
            onAddClick = {},
            notifications = emptyList(),
            nextChoreName = "Nessuna",
            nextChoreDeadline = "",
            totalDebtAmount = "0,00 €"
        )
    }
}

@Preview(showBackground = true, name = "3. Populated State")
@Composable
fun HouseDashboardPreviewPopulated() {
    ProgettoMobileTheme {
        HouseDashboardView(
            nomeUtente = "Ale",
            imageBytes = null,
            isLoading = false,
            userToken = "my_token",
            houseAddress = "Via Garibaldi 12",
            onAddClick = {},
            notifications = mockNotifications,
            nextChoreName = "Bagno",
            nextChoreDeadline = "Oggi",
            totalDebtAmount = "15,50 €"
        )
    }
}