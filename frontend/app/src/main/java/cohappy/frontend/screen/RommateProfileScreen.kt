package cohappy.frontend.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.model.HouseProfileViewModel
import cohappy.frontend.view.house.HouseProfileView

@Composable
fun HouseProfileScreen(
    userToken: String,
    houseCode: String,
    onLogoutClick: () -> Unit,
    onLeaveHouseSuccess: () -> Unit,
    onRulesClick: () -> Unit,
    onRoommatesClick: () -> Unit,
    onPasswordChangeClick: () -> Unit,
    viewModel: HouseProfileViewModel = viewModel()
) {
    LaunchedEffect(userToken) {
        viewModel.loadProfile(userToken)
    }

    LaunchedEffect(viewModel.hasLeftHouse) {
        if (viewModel.hasLeftHouse) {
            onLeaveHouseSuccess()
        }
    }

    HouseProfileView(
        userName = viewModel.userName,
        userSurname = viewModel.userSurname,
        imageBytes = viewModel.profileImageBytes,
        isLoading = viewModel.isLoading,
        onLeaveHouseClick = { viewModel.leaveHouse(userToken, houseCode) },
        onLogoutClick = onLogoutClick,
        onRulesClick = onRulesClick,
        onRoommatesClick = onRoommatesClick,
        onPasswordChangeClick = onPasswordChangeClick
    )
}