package cohappy.frontend.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.model.HouseDashboardViewModel
import cohappy.frontend.view.house.HomeGestionaleView

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

    HomeGestionaleView(
        nomeUtente = viewModel.nomeUtente,
        imageBytes = viewModel.profileImageBytes,
        isLoading = viewModel.isLoading,
        onAddClick = {
        },
        notifications = viewModel.notifications,
        nextChore = viewModel.nextChore,
        totalDebt = viewModel.totalDebt,
        userToken = userToken,
        onChoreClick = onChoreClick,
        onWalletClick = onWalletClick
    )
}