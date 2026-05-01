package cohappy.frontend.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.client.dto.response.GetNotificationDTO
import cohappy.frontend.ui.theme.ProgettoMobileTheme
import cohappy.frontend.view.house.HouseDashboardView

@Preview(showBackground = true)
@Composable
fun HouseDashboardPreview() {
    ProgettoMobileTheme {
        HouseDashboardView(
            nomeUtente = "Ale",
            imageBytes = null,
            isLoading = false,
            userToken = "token_finto",
            onAddClick = {},
            notifications = listOf(
                GetNotificationDTO(
                    eventId = "1",
                    eventType = "CHAT",
                    title = "Marco",
                    subtitle = "Ciao, usciamo?",
                    timestamp = "2026-04-30T18:30:00",
                    imageBytes = null,
                    userCode = "token_finto"
                ),
                GetNotificationDTO(
                    eventId = "2",
                    eventType = "CHORE",
                    title = "Pulizie",
                    subtitle = "Tocca a te pulire il bagno",
                    timestamp = "2026-04-30T18:30:00",
                    imageBytes = null,
                    userCode = "token_finto"
                )
            ),
            nextChoreName = "Bagno",
            totalDebtAmount = "15.50 €"
        )
    }
}

