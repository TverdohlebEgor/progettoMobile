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
import cohappy.frontend.model.RommateProfileViewModel
import cohappy.frontend.view.house.HouseProfileView
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
    val coroutineScope = rememberCoroutineScope() // 💅 CI SERVE PER LANCIARE L'USCITA!

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

    // Teniamo questo per sicurezza nel caso in cui il ViewModel si svegli
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
        houseCode = viewModel.currentHouseCode.ifBlank { houseCode },
        isUpdatingCode = viewModel.isUpdatingCode,
        codeUpdateError = viewModel.codeUpdateError,
        isCurrentUserAdmin = viewModel.isCurrentUserAdmin,
        onUpdateCodeClick = { nuovoCodice ->
            viewModel.updateHouseCode(viewModel.currentHouseCode.ifBlank { houseCode }, nuovoCodice)
        },
        onEditPhotoClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onLeaveHouseClick = {
            // 💅 MAGIC BADDIE: Facciamo il tentativo di uscita direttamente da qui!
            coroutineScope.launch {
                try {
                    val tokenPulito = userToken.replace("\"", "").trim()
                    val activeCode = viewModel.currentHouseCode.ifBlank { houseCode }
                    val dto = RemoveUserDTO(houseCode = activeCode, userCode = tokenPulito)

                    val response = withContext(Dispatchers.IO) {
                        ClientSingleton.houseApi.removeUser(dto)
                    }

                    // 🚀 SE VA: TI SPARIAMO IN ADS MAIN!
                    if (response.isSuccessful) {
                        onLeaveHouseSuccess()
                    } else {
                        // 🛑 SE FALLISCE: TI AVVISIAMO DEL PERCHÉ!
                        Toast.makeText(context, "Impossibile uscire. Errore: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Nessuna connessione al server", Toast.LENGTH_SHORT).show()
                }
            }
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