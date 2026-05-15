package cohappy.frontend.preview.theme.dark

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.client.dto.enum.DebtType
import cohappy.frontend.ui.theme.ProgettoMobileTheme
import cohappy.frontend.view.house.PortfolioView
import cohappy.frontend.viewmodel.PortfolioTransaction
import cohappy.frontend.viewmodel.TransactionShare

private val mockTransactionsDark = listOf(
    PortfolioTransaction(
        id = "1",
        isDebt = false,
        title = "Spesa Esselunga",
        subtitle = "Oggi • Hai pagato tu",
        amount = 24.50,
        category = DebtType.GROCERIE,
        shares = listOf(
            TransactionShare("1", "Tu", 24.50, true),
            TransactionShare("2", "Marco", 12.25, false),
            TransactionShare("3", "Sofia", 12.25, true)
        ),
        beneficiaryName = "Tu",
        totalAmount = 49.0
    ),
    PortfolioTransaction(
        id = "2",
        myDebtId = "debt2",
        isDebt = true,
        title = "Bolletta Luce",
        subtitle = "Ieri • Ha pagato Marco",
        amount = 15.00,
        category = DebtType.BILL,
        shares = listOf(
            TransactionShare("1", "Marco", 15.00, true),
            TransactionShare("2", "Tu", 15.00, false)
        ),
        beneficiaryName = "Marco",
        totalAmount = 30.0
    ),
    PortfolioTransaction(
        id = "3",
        isDebt = true,
        title = "Sushi Delivery",
        subtitle = "Ven 12 • Ha pagato Sofia",
        amount = 22.00,
        category = DebtType.DELIVERY_AND_EATING_OUT,
        shares = listOf(
            TransactionShare("1", "Sofia", 22.00, true),
            TransactionShare("2", "Tu", 22.00, true)
        ),
        beneficiaryName = "Sofia",
        totalAmount = 44.0
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
            totalDebts = 37.00,
            totalCredits = 24.50,
            activeFilter = "ALL",
            transactions = mockTransactionsDark,
            onFilterChange = {},
            userToken = "",
            onAddClick = { println("Add Clicked") }
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
            transactions = listOf(mockTransactionsDark[0]),
            onFilterChange = {},
            userToken = "",
            onAddClick = { println("Add Clicked") }
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "3. Portfolio Paid - Dark"
)
@Composable
fun PreviewPortfolioPaidDark() {
    ProgettoMobileTheme {
        PortfolioView(
            isLoading = false,
            totalDebts = 37.00,
            totalCredits = 24.50,
            activeFilter = "PAID",
            transactions = listOf(mockTransactionsDark[2]),
            onFilterChange = {},
            userToken = "",
            onAddClick = { println("Add Clicked") }
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "4. Portfolio Empty List - Dark"
)
@Composable
fun PreviewPortfolioEmptyListDark() {
    ProgettoMobileTheme {
        PortfolioView(
            isLoading = false,
            totalDebts = 0.0,
            totalCredits = 0.0,
            activeFilter = "ALL",
            transactions = listOf(),
            onFilterChange = {},
            userToken = "",
            onAddClick = { println("Add Clicked") }
        )
    }
}
