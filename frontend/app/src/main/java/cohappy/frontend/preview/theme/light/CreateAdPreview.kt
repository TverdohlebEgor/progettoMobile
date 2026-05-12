package cohappy.frontend.preview.theme.light

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.client.dto.enum.HouseStateEnum
import cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO
import cohappy.frontend.util.randomPhoto
import cohappy.frontend.view.ad.AdListView
import cohappy.frontend.view.ad.CreateAdView

@Preview(showBackground = true)
@Composable
fun PreviewCreateAd() {
    MaterialTheme {
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