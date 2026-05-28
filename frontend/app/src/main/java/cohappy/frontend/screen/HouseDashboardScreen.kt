package cohappy.frontend.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.view.house.HouseDashboardView
import cohappy.frontend.viewmodel.HouseDashboardViewModel

@Composable
fun HouseDashboardScreen(
    userToken: String?,
    houseCode: String,
    onProfileClick: () -> Unit,
    onChoreClick: () -> Unit = {},
    onWalletClick: () -> Unit = {},
    viewModel: HouseDashboardViewModel = viewModel()
) {
    val cleanToken = userToken ?: ""

    LaunchedEffect(cleanToken) {
        if (cleanToken.isNotBlank()) {
            viewModel.loadDashboardData(cleanToken, houseCode)
        }
    }

    HouseDashboardView(
        nomeUtente = viewModel.nomeUtente,
        imageBytes = viewModel.profileImageBytes,
        isLoading = viewModel.isLoading,
        userToken = cleanToken,
        houseAddress = viewModel.houseAddress,
        notifications = viewModel.notifications,
        nextChoreName = viewModel.nextChoreName,
        nextChoreDeadline = viewModel.nextChoreDeadline,
        totalDebtAmount = viewModel.totalDebtAmount,
        onProfileClick = onProfileClick,
        onChoreClick = onChoreClick,
        onWalletClick = onWalletClick,
        onAddClick = { }
    )
}