package cohappy.frontend.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.model.ChoresViewModel
import cohappy.frontend.model.PortfolioTransaction
import cohappy.frontend.view.house.ChoresView
import cohappy.frontend.view.house.PortfolioView

@Composable
fun PortfolioScreen(
    userToken: String,
    viewModel: ChoresViewModel = viewModel()
) {
    LaunchedEffect(userToken) {
        viewModel.loadUserData(userToken)
    }

    PortfolioView(
        isLoading = viewModel.isLoading,
        userToken = userToken,
        totalDebts = 45.00,//viewModel.totalDebts,
        totalCredits = 50.00,
        activeFilter = "ALL",
        transactions = listOf(
            PortfolioTransaction("1", false, "Spesa Esselunga", "Oggi • Hai pagato tu", 24.50),
            PortfolioTransaction("2", true, "Bolletta Luce", "Ieri • Ha pagato Marco", 15.00)
        ),
        onFilterChange = {}
    )
}

