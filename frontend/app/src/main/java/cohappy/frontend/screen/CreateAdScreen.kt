package cohappy.frontend.screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.view.ad.CreateAdView
import cohappy.frontend.viewmodel.CreateAdViewModel

@Composable
fun CreateAdScreen(
    userToken: String,
    houseCode: String,
    onBackClick: () -> Unit,
    onAdPublished: () -> Unit,
    viewModel: CreateAdViewModel = viewModel()
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
                    viewModel.addImage(bytes)
                    Toast.makeText(context, "Foto aggiunta con successo!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Errore nel caricamento della foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(viewModel.isSuccess) {
        if (viewModel.isSuccess) {
            Toast.makeText(context, "Annuncio pubblicato con successo!", Toast.LENGTH_LONG).show()
            viewModel.resetSuccess()
            onAdPublished()
        }
    }

    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            viewModel.resetError()
        }
    }

    CreateAdView(
        prezzo = viewModel.price,
        descrizione = viewModel.description,
        onPrezzoChange = { viewModel.updatePrice(it) },
        onDescrizioneChange = { viewModel.updateDescription(it) },
        onAddPhotoClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onPublishClick = { viewModel.publishAdvertisement(houseCode, userToken) },
        onBackClick = onBackClick
    )
}