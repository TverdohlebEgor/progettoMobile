package cohappy.frontend.preview.theme.dark

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.client.dto.enum.DebtType
import cohappy.frontend.ui.theme.ProgettoMobileTheme
import cohappy.frontend.view.house.PortfolioView
import cohappy.frontend.viewmodel.PortfolioTransaction

private val mockTransactionsDark = listOf(
    PortfolioTransaction(
        "1",
        false,
        "Spesa Esselunga",
        "Oggi • Hai pagato tu",
        24.50,
        category = DebtType.GROCERIE
    ),
    PortfolioTransaction(
        "2",
        true,
        "Bolletta Luce",
        "Ieri • Ha pagato Marco",
        15.00,
        category = DebtType.BILL
    ),
    PortfolioTransaction(
        "3",
        true,
        "Sushi Delivery",
        "Ven 12 • Ha pagato Sofia",
        22.00,
        category = DebtType.DELIVERY_AND_EATING_OUT
    ),
    PortfolioTransaction(
        "4",
        false,
        "Detersivi e Saponi",
        "Mer 10 • Hai pagato tu",
        8.00,
        category = DebtType.GROCERIE
    )
)

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "1. Portfolio In Debt - Dark"
)
@Composable
fun PreviewPortfolioInDebtDark() {
    ProgettoMobileTheme {
        PortfolioView(
            isLoading = false,
            totalDebts = -15.50,
            totalCredits = 24.50,
            activeFilter = "ALL",
            transactions = mockTransactionsDark,
            onFilterChange = {},
            userToken = "",
            onAddClick = { }
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "2. Portfolio In Credit - Dark"
)
@Composable
fun PreviewPortfolioInCreditDark() {
    ProgettoMobileTheme {
        PortfolioView(
            isLoading = false,
            totalDebts = 0.0,
            totalCredits = 24.50,
            activeFilter = "ALL",
            transactions = mockTransactionsDark,
            onFilterChange = {},
            userToken = "",
            onAddClick = { }
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "3. Portfolio Empty List - Dark"
)
@Composable
fun PreviewPortfolioEmptyListDark() {
    ProgettoMobileTheme {
        PortfolioView(
            isLoading = false,
            totalDebts = 0.0,
            totalCredits = 24.50,
            activeFilter = "ALL",
            transactions = listOf(),
            onFilterChange = {},
            userToken = "",
            onAddClick = { }
        )
    }
}
