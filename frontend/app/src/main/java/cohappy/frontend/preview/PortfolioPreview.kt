package cohappy.frontend.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.model.PortfolioTransaction
import cohappy.frontend.view.house.PortfolioView


@Preview(showBackground = true)
@Composable
fun PreviewPortfolio() {
    MaterialTheme {
        PortfolioView(
            isLoading = false,
            totalDebts = -15.50,
            totalCredits = 24.50,
            activeFilter = "ALL",
            transactions = listOf(
                PortfolioTransaction("1", false, "Spesa Esselunga", "Oggi • Hai pagato tu", 24.50),
                PortfolioTransaction("2", true, "Bolletta Luce", "Ieri • Ha pagato Marco", 15.00),
                PortfolioTransaction("3", true, "Sushi Delivery", "Ven 12 • Ha pagato Sofia", 22.00),
                PortfolioTransaction("4", false, "Detersivi e Saponi", "Mer 10 • Hai pagato tu", 8.00)
            ),
            onFilterChange = {},
            userToken = ""
        )
    }
}
