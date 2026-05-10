package cohappy.frontend.preview.theme.dark

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.ui.theme.ProgettoMobileTheme
import cohappy.frontend.view.ad.CreateAdView

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
    ProgettoMobileTheme {
        CreateAdView(
            prezzo = "450",
            descrizione = "Bellissima stanza singola in centro storico. Letto matrimoniale, scrivania enorme e armadio a 4 ante. La casa è da condividere con altre due ragazze super chill. Cucina attrezzata e bagno appena ristrutturato. Libera da subito!",
            onPrezzoChange = {},
            onDescrizioneChange = {},
            onAddPhotoClick = {},
            onPublishClick = {},
            onBackClick = {}
        )
    }
}