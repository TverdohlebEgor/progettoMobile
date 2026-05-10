package cohappy.frontend.preview.theme.light

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.ui.theme.ProgettoMobileTheme
import cohappy.frontend.view.ad.CreateAdView

@Preview(showBackground = true, name = "1. Empty State")
@Composable
fun CreateAdPreviewEmpty() {
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

@Preview(showBackground = true, name = "2. Filled State")
@Composable
fun CreateAdPreviewFilled() {
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