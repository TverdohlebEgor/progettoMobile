package cohappy.frontend.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.model.HouseDashboardViewModel

import cohappy.frontend.view.house.HouseDashboardView

@Composable
fun HouseDashboardScreen(
    userToken: String,
    viewModel: HouseDashboardViewModel = viewModel(),
    onChoreClick: (String) -> Unit,
    onWalletClick: (String) -> Unit
) {
    LaunchedEffect(userToken) {
        viewModel.loadDashboardData(userToken)
    }

    HouseDashboardView(
        nomeUtente = viewModel.nomeUtente,
        imageBytes = viewModel.profileImageBytes,
        isLoading = viewModel.isLoading,
        onAddClick = {
        },
        notifications = viewModel.notifications,
//        nextChore = viewModel.nextChore,
//        totalDebt = viewModel.totalDebt,
        userToken = userToken,
        nextChoreName = "Bagno",
        totalDebtAmount = "35",
//        onChoreClick = onChoreClick,
//        onWalletClick = onWalletClick
    )
}