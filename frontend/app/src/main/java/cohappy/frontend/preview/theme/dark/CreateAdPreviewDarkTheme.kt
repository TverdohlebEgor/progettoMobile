package cohappy.frontend.preview.theme.dark

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.R
import cohappy.frontend.ui.theme.ProgettoMobileTheme
import cohappy.frontend.view.ad.CreateAdView
import java.io.ByteArrayOutputStream

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "1. Dark Empty State"
)
@Composable
fun CreateAdDarkPreviewEmpty() {
    ProgettoMobileTheme {
        CreateAdView(
            prezzo = "",
            descrizione = "",
            immagine = null,
            onPrezzoChange = {},
            onDescrizioneChange = {},
            onAddPhotoClick = {},
            onPublishClick = {},
            onBackClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "2. Dark Filled State"
)
@Composable
fun CreateAdDarkPreviewFilled() {
    val context = LocalContext.current
    val imageBytes = remember {
        val options = BitmapFactory.Options().apply {
            inSampleSize = 4 // Ridimensiona l'immagine per evitare errori di memoria nella preview
        }
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.casa1, options)
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, stream) // Qualità 80 per risparmiare spazio
        stream.toByteArray()
    }

    ProgettoMobileTheme {
        CreateAdView(
            prezzo = "450",
            descrizione = "Bellissima stanza singola in centro storico. Letto matrimoniale, scrivania enorme e armadio a 4 ante. La casa è da condividere con altre due ragazze super chill. Cucina attrezzata e bagno appena ristrutturato. Libera da subito!",
            immagine = imageBytes,
            onPrezzoChange = {},
            onDescrizioneChange = {},
            onAddPhotoClick = {},
            onPublishClick = {},
            onBackClick = {}
        )
    }
}
