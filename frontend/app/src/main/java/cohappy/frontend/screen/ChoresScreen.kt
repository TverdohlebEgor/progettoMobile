package cohappy.frontend.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.model.ChoreViewModel
import cohappy.frontend.view.house.ChoresView

@Composable
fun ChoresScreen(
    userToken: String,
    viewModel: ChoreViewModel = viewModel()
) {
    LaunchedEffect(userToken) {
        if (userToken.isNotBlank()) {
            viewModel.loadUserData(userToken)
        }
    }
    ChoresView(
        nomeUtente = viewModel.nomeUtente,
        imageBytes = null,
        isLoading = viewModel.isLoading,
        userToken = userToken,
        chores = viewModel.chores,
        onChoreToggle = { choreCode, assignedToUser, newStatus ->
            viewModel.toggleChoreCompletion(choreCode, assignedToUser, newStatus)
        }
    )
}