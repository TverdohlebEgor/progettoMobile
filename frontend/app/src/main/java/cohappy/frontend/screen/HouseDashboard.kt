package cohappy.frontend.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.model.HouseDashboardViewModel
import cohappy.frontend.view.house.HomeGestionaleView

@Composable
fun HomeGestionaleScreen(
    userToken: String,
    viewModel: HouseDashboardViewModel = viewModel()
) {
    LaunchedEffect(userToken) {
        viewModel.loadDashboardData(userToken)
    }

    HomeGestionaleView(
        nomeUtente = viewModel.nomeUtente,
        imageBytes = viewModel.profileImageBytes,
        isLoading = viewModel.isLoading,
        onAddClick = {
        }
    )
}