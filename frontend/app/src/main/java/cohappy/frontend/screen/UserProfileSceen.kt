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
import cohappy.frontend.model.UserProfileViewModel
import cohappy.frontend.view.ad.UserProfileView

@Composable
fun UserProfileScreen(
    userToken: String,
    onLogoutClick: () -> Unit,
    onCreateHouseClick: () -> Unit,
    onJoinConfirmClick: (String) -> Unit,
    viewModel: UserProfileViewModel = viewModel()
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
                Log.e("UserProfileScreen", "Errore lettura foto", e)
            }
        }
    }


    LaunchedEffect(userToken) {
        viewModel.loadProfile(userToken)
    }

    UserProfileView(
        userName = viewModel.userName,
        userSurname = viewModel.userSurname,
        imageBytes = viewModel.profileImageBytes,
        isLoading = viewModel.isLoading,
        onEditClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onCreateHouseClick = onCreateHouseClick,
        onJoinConfirmClick = onJoinConfirmClick,
        onLogoutClick = onLogoutClick
    )
}