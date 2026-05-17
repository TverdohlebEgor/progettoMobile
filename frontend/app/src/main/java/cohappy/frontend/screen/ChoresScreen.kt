package cohappy.frontend.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.view.house.ChoresView
import cohappy.frontend.viewmodel.ChoreViewModel

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
        selectedDate = viewModel.selectedDate,
        onDateSelected = { viewModel.onDateSelected(it, userToken) },
        chores = viewModel.chores,
        daysWithChores = viewModel.daysWithChores,
        onChoreToggle = { choreCode, assignedToUser, newStatus ->
            viewModel.toggleChoreCompletion(choreCode, assignedToUser, newStatus)
        },
        onAddChoreConfirm = { name, desc, dates, user, isRecursive ->
            viewModel.createChore(
                userToken = userToken,
                name = name,
                description = desc,
                dates = dates,
                assignedTo = user,
                isRecursive = isRecursive,
                onSuccess = { /* Automagic update since it's a state */ }
            )
        },
        roommates = viewModel.roommates,
        currentUserCode = viewModel.currentUserCode ?: ""
    )
}
