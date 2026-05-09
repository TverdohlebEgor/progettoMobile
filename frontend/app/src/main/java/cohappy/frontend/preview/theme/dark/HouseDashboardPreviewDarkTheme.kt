package cohappy.frontend.preview.theme.dark

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.client.dto.response.GetNotificationDTO
import cohappy.frontend.ui.theme.ProgettoMobileTheme
import cohappy.frontend.view.house.HouseDashboardView

private val mockNotificationsDark = listOf(
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

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "1. Dark Loading State"
)
@Composable
fun HouseDashboardDarkPreviewLoading() {
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

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "2. Dark Empty State"
)
@Composable
fun HouseDashboardDarkPreviewEmpty() {
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

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "3. Dark Populated State"
)
@Composable
fun HouseDashboardDarkPreviewPopulated() {
    ProgettoMobileTheme {
        HouseDashboardView(
            nomeUtente = "Ale",
            imageBytes = null,
            isLoading = false,
            userToken = "my_token",
            houseAddress = "Via Garibaldi 12",
            onAddClick = {},
            notifications = mockNotificationsDark,
            nextChoreName = "Bagno",
            nextChoreDeadline = "Oggi",
            totalDebtAmount = "15,50 €"
        )
    }
}