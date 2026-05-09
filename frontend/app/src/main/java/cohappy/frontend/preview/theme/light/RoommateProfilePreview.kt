package cohappy.frontend.preview.theme.light

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.ui.theme.ProgettoMobileTheme
import cohappy.frontend.view.house.HouseProfileView
import cohappy.frontend.viewmodel.RoommateItem

private val mockRoommates = listOf(
    RoommateItem("1", "Alessandro", "Bianchi", isAdmin = true, isMe = true),
    RoommateItem("2", "Marco", "Rossi", isAdmin = false, isMe = false),
    RoommateItem("3", "Sofia", "Verdi", isAdmin = false, isMe = false)
)

@Preview(showBackground = true, name = "1. Loading State")
@Composable
fun RommateProfilePreviewLoading() {
    ProgettoMobileTheme {
        HouseProfileView(
            userName = "Alessandro",
            userSurname = "Bianchi",
            imageBytes = null,
            isLoading = true,
            houseAddress = "Via Roma 123, Milano",
            houseCode = "ABC123XYZ",
            onUpdateCodeClick = {},
            onEditPhotoClick = {},
            onLeaveHouseClick = {},
            onLogoutClick = {},
            onRoommatesClick = {},
            onCreateAdClick = {}
        )
    }
}

@Preview(showBackground = true, name = "2. Normal User State")
@Composable
fun RommateProfilePreviewNormal() {
    ProgettoMobileTheme {
        HouseProfileView(
            userName = "Alessandro",
            userSurname = "Bianchi",
            imageBytes = null,
            isLoading = false,
            houseAddress = "Via Roma 123, Milano",
            houseCode = "ABC123XYZ",
            isCurrentUserAdmin = false,
            onUpdateCodeClick = {},
            onEditPhotoClick = {},
            onLeaveHouseClick = {},
            onLogoutClick = {},
            onRoommatesClick = {},
            onCreateAdClick = {}
        )
    }
}

@Preview(showBackground = true, name = "3. Admin User State")
@Composable
fun RommateProfilePreviewAdmin() {
    ProgettoMobileTheme {
        HouseProfileView(
            userName = "Alessandro",
            userSurname = "Bianchi",
            imageBytes = null,
            isLoading = false,
            houseAddress = "Via Roma 123, Milano",
            houseCode = "ABC123XYZ",
            isCurrentUserAdmin = true,
            onUpdateCodeClick = {},
            onEditPhotoClick = {},
            onLeaveHouseClick = {},
            onLogoutClick = {},
            onRoommatesClick = {},
            onCreateAdClick = {}
        )
    }
}

@Preview(showBackground = true, name = "4. Code Updating State")
@Composable
fun RommateProfilePreviewUpdatingCode() {
    ProgettoMobileTheme {
        HouseProfileView(
            userName = "Alessandro",
            userSurname = "Bianchi",
            imageBytes = null,
            isLoading = false,
            houseAddress = "Via Roma 123, Milano",
            houseCode = "ABC123XYZ",
            isUpdatingCode = true,
            isCurrentUserAdmin = true,
            onUpdateCodeClick = {},
            onEditPhotoClick = {},
            onLeaveHouseClick = {},
            onLogoutClick = {},
            onRoommatesClick = {},
            onCreateAdClick = {}
        )
    }
}

@Preview(showBackground = true, name = "5. Code Update Error")
@Composable
fun RommateProfilePreviewError() {
    ProgettoMobileTheme {
        HouseProfileView(
            userName = "Alessandro",
            userSurname = "Bianchi",
            imageBytes = null,
            isLoading = false,
            houseAddress = "Via Roma 123, Milano",
            houseCode = "ABC123XYZ",
            codeUpdateError = "Codice non valido o già esistente",
            isCurrentUserAdmin = true,
            onUpdateCodeClick = {},
            onEditPhotoClick = {},
            onLeaveHouseClick = {},
            onLogoutClick = {},
            onRoommatesClick = {},
            onCreateAdClick = {}
        )
    }
}

@Preview(showBackground = true, name = "6. Roommates Popup Loading")
@Composable
fun RommateProfilePreviewPopupLoading() {
    ProgettoMobileTheme {
        HouseProfileView(
            userName = "Alessandro",
            userSurname = "Bianchi",
            imageBytes = null,
            isLoading = false,
            houseAddress = "Via Roma 123, Milano",
            houseCode = "ABC123XYZ",
            showRoommatesPopup = true,
            isRoommatesLoading = true,
            isCurrentUserAdmin = true,
            onUpdateCodeClick = {},
            onEditPhotoClick = {},
            onLeaveHouseClick = {},
            onLogoutClick = {},
            onRoommatesClick = {},
            onCreateAdClick = {},
            onDismissRoommatesPopup = {}
        )
    }
}

@Preview(showBackground = true, name = "7. Roommates Popup Populated")
@Composable
fun RommateProfilePreviewPopupPopulated() {
    ProgettoMobileTheme {
        HouseProfileView(
            userName = "Alessandro",
            userSurname = "Bianchi",
            imageBytes = null,
            isLoading = false,
            houseAddress = "Via Roma 123, Milano",
            houseCode = "ABC123XYZ",
            showRoommatesPopup = true,
            roommatesList = mockRoommates,
            isCurrentUserAdmin = true,
            onUpdateCodeClick = {},
            onEditPhotoClick = {},
            onLeaveHouseClick = {},
            onLogoutClick = {},
            onRoommatesClick = {},
            onCreateAdClick = {},
            onDismissRoommatesPopup = {},
            onPromoteClick = {},
            onKickClick = {}
        )
    }
}
