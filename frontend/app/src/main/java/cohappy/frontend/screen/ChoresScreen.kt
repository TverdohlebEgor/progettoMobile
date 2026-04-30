package cohappy.frontend.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.model.ChoresViewModel
import cohappy.frontend.view.house.ChoresView

@Composable
fun ChoresScreen(
    userToken: String,
    viewModel: ChoresViewModel = viewModel()
) {
    LaunchedEffect(userToken) {
        viewModel.loadUserData(userToken)
    }

    ChoresView(
        nomeUtente = viewModel.nomeUtente,
        imageBytes = null,
        isLoading = viewModel.isLoading,
        userToken = userToken,
        onChoreToggle = { choreCode, assignedToUser, newStatus ->
            viewModel.toggleChoreCompletion(choreCode, assignedToUser, newStatus)
        },
        chores = viewModel.chores,

        )
}