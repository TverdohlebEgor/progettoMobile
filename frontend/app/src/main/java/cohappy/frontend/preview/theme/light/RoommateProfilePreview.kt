package cohappy.frontend.preview.theme.light

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.ui.theme.ProgettoMobileTheme
import cohappy.frontend.view.house.HouseProfileView
import cohappy.frontend.viewmodel.RoommateItem

private val mockRoommates = listOf(
    RoommateItem(userCode = "1", name = "Ale", surname = "Rossi", isAdmin = true, isMe = true),
    RoommateItem(userCode = "2", name = "Sofia", surname = "Bianchi", isAdmin = false, isMe = false),
    RoommateItem(userCode = "3", name = "Marco", surname = "Verdi", isAdmin = false, isMe = false)
)

@Preview(showBackground = true, name = "1. Loading State")
@Composable
fun RoommateProfilePreviewLoading() {
    ProgettoMobileTheme {
        HouseProfileView(
            userName = "Caricamento...",
            userSurname = "",
            imageBytes = null,
            isLoading = true,
            houseAddress = "",
            houseCode = "",
            hasExistingAd = false
        )
    }
}

@Preview(showBackground = true, name = "2. Normal User State")
@Composable
fun RoommateProfilePreviewNormal() {
    ProgettoMobileTheme {
        HouseProfileView(
            userName = "Ale",
            userSurname = "Rossi",
            imageBytes = null,
            isLoading = false,
            houseAddress = "Via Garibaldi 12",
            houseCode = "COH-1234",
            isCurrentUserAdmin = false,
            hasExistingAd = false
        )
    }
}

@Preview(showBackground = true, name = "3. Update Code Error")
@Composable
fun RoommateProfilePreviewCodeError() {
    ProgettoMobileTheme {
        HouseProfileView(
            userName = "Ale",
            userSurname = "Rossi",
            imageBytes = null,
            isLoading = false,
            houseAddress = "Via Garibaldi 12",
            houseCode = "COH-1234",
            codeUpdateError = "Questo codice è già in uso!",
            hasExistingAd = false
        )
    }
}


@Preview(showBackground = true, name = "4. Roommates Loading Popup")
@Composable
fun RoommateProfilePreviewRoommatesLoading() {
    ProgettoMobileTheme {
        HouseProfileView(
            userName = "Ale",
            userSurname = "Rossi",
            imageBytes = null,
            isLoading = false,
            houseAddress = "Via Garibaldi 12",
            houseCode = "COH-1234",
            showRoommatesPopup = true,
            isRoommatesLoading = true,
            hasExistingAd = false
        )
    }
}


@Preview(showBackground = true, name = "5. Roommates Loaded (Admin)")
@Composable
fun RoommateProfilePreviewRoommatesLoaded() {
    ProgettoMobileTheme {
        HouseProfileView(
            userName = "Ale",
            userSurname = "Rossi",
            imageBytes = null,
            isLoading = false,
            houseAddress = "Via Garibaldi 12",
            houseCode = "COH-1234",
            showRoommatesPopup = true,
            isRoommatesLoading = false,
            roommatesList = mockRoommates,
            isCurrentUserAdmin = true,
            hasExistingAd = true
        )
    }
}