package cohappy.frontend.screen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
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
            } catch (e: Exception) {
                Log.e("HouseProfileScreen", "Errore lettura foto", e)
            }
        }
    }

    LaunchedEffect(userToken) {
        viewModel.loadProfile(userToken)
    }

    LaunchedEffect(houseCode) {
        viewModel.loadHouseDetails(houseCode)
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
        houseAddress = viewModel.houseAddress,
        houseCode = houseCode.ifBlank { "COH-8X2P" },
        isUpdatingCode = viewModel.isUpdatingCode,
        codeUpdateError = viewModel.codeUpdateError,
        onUpdateCodeClick = { nuovoCodice ->
            viewModel.updateHouseCode(oldHouseCode = houseCode, newHouseCode = nuovoCodice)
        },
        // 💅 COLLEGHIAMO LA VIA AL CERVELLO!
        isUpdatingAddress = viewModel.isUpdatingAddress,
        addressUpdateError = viewModel.addressUpdateError,
        onUpdateAddressClick = { nuovaVia ->
            viewModel.updateHouseAddress(houseCode = houseCode, newAddress = nuovaVia)
        },
        onEditPhotoClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onLeaveHouseClick = { viewModel.leaveHouse(userToken, houseCode) },
        onLogoutClick = onLogoutClick,
        onRoommatesClick = { viewModel.openRoommatesPopup(houseCode, userToken) },
        onPasswordChangeClick = onPasswordChangeClick,
        showRoommatesPopup = viewModel.showRoommatesPopup,
        roommatesList = viewModel.roommatesList,
        isCurrentUserAdmin = viewModel.isCurrentUserAdmin,
        isRoommatesLoading = viewModel.isRoommatesLoading,
        onDismissRoommatesPopup = { viewModel.closeRoommatesPopup() },
        onPromoteClick = { targetCode -> viewModel.promoteToAdmin(houseCode, targetCode, userToken) },
        onKickClick = { targetCode -> viewModel.kickUser(houseCode, targetCode, userToken) }
    )
}