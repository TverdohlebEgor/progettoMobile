package cohappy.frontend.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.model.ChoresViewModel
import cohappy.frontend.model.PortfolioTransaction
import cohappy.frontend.model.PortfolioViewModel
import cohappy.frontend.view.house.ChoresView
import cohappy.frontend.view.house.PortfolioView

@Composable
fun PortfolioScreen(
    userToken: String,
    viewModel: PortfolioViewModel = viewModel()
) {
    LaunchedEffect(userToken) {
        viewModel.loadPortfolio(userToken)
    }

    PortfolioView(
        isLoading = viewModel.isLoading,
        userToken = userToken,
        totalDebts = 45.00,//viewModel.totalDebts,
        totalCredits = 50.00,
        activeFilter = "ALL",
        transactions = listOf(
            PortfolioTransaction("1", false, "Spesa Esselunga", "Oggi • Hai pagato tu", 24.50),
            PortfolioTransaction("2", true, "Bolletta Luce", "Ieri • Ha pagato Marco", 15.00),
            PortfolioTransaction("3", true, "Sushi Delivery", "Ven 12 • Ha pagato Sofia", 22.00),
            PortfolioTransaction("4", false, "Detersivi e Saponi", "Mer 10 • Hai pagato tu", 8.00),
            PortfolioTransaction("5", true, "Abbonamento Netflix", "Lun 08 • Ha pagato Egor", 4.50),
            PortfolioTransaction("6", true, "Affitto Maggio", "01 Mag • Ha pagato Anna", 350.00),
            PortfolioTransaction("7", false, "Cocktail Bar", "30 Apr • Hai pagato tu", 18.00),
            PortfolioTransaction("8", true, "Wi-Fi Casa", "28 Apr • Ha pagato Marco", 12.50),
            PortfolioTransaction("9", false, "Spesa Conad", "25 Apr • Hai pagato tu", 45.20),
            PortfolioTransaction("10", true, "Idraulico", "20 Apr • Ha pagato Sofia", 50.00),
            PortfolioTransaction("11", false, "Pizza d'asporto", "18 Apr • Hai pagato tu", 15.00),
            PortfolioTransaction("12", true, "Carta Igienica", "15 Apr • Ha pagato Anna", 3.50)
        ),
        onFilterChange = {}
    )
}

