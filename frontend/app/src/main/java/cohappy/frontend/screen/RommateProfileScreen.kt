package cohappy.frontend.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.RemoveUserDTO
import cohappy.frontend.view.house.HouseProfileView
import cohappy.frontend.viewmodel.RommateProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RommateProfileScreen(
    userToken: String,
    houseCode: String,
    onLogoutClick: () -> Unit,
    onLeaveHouseSuccess: () -> Unit,
    onRoommatesClick: () -> Unit,
    onCreateAdClick: () -> Unit = {},
    viewModel: RommateProfileViewModel = viewModel()
) {
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                if (bytes != null) {
                    viewModel.uploadNewImage(userToken, bytes)
                }
            } catch (e: Exception) {}
        }
    }

    LaunchedEffect(userToken) {
        viewModel.loadProfile(userToken)
    }

    LaunchedEffect(houseCode) {
        viewModel.loadHouseDetails(houseCode, userToken)
    }


    LaunchedEffect(viewModel.hasLeftHouse) {
        if (viewModel.hasLeftHouse) {
            onLeaveHouseSuccess()
        }
    }

    LaunchedEffect(viewModel.leaveHouseError) {
        viewModel.leaveHouseError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    HouseProfileView(
        userName = viewModel.userName,
        userSurname = viewModel.userSurname,
        imageBytes = viewModel.profileImageBytes,
        isLoading = viewModel.isLoading,
        houseAddress = viewModel.houseAddress,
        houseCode = viewModel.currentHouseCode.ifBlank { houseCode },
        isUpdatingCode = viewModel.isUpdatingCode,
        codeUpdateError = viewModel.codeUpdateError,
        isCurrentUserAdmin = viewModel.isCurrentUserAdmin,
        hasExistingAd = viewModel.hasExistingAd,
        onUpdateCodeClick = { nuovoCodice ->
            viewModel.updateHouseCode(viewModel.currentHouseCode.ifBlank { houseCode }, nuovoCodice)
        },
        onEditPhotoClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onLeaveHouseClick = {
            viewModel.leaveHouse(userToken, houseCode)
        },
        onLogoutClick = onLogoutClick,
        onRoommatesClick = {
            viewModel.openRoommatesPopup(houseCode, userToken)
            onRoommatesClick()
        },
        onCreateAdClick = onCreateAdClick,
        showRoommatesPopup = viewModel.showRoommatesPopup,
        roommatesList = viewModel.roommatesList,
        isRoommatesLoading = viewModel.isRoommatesLoading,
        onDismissRoommatesPopup = { viewModel.closeRoommatesPopup() },
        onPromoteClick = { targetCode -> viewModel.promoteToAdmin(houseCode, targetCode, userToken) },
        onKickClick = { targetCode -> viewModel.kickUser(houseCode, targetCode, userToken) }
    )
}