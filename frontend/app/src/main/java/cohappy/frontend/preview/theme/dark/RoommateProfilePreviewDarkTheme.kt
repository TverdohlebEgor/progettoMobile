package cohappy.frontend.preview.theme.dark

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.ui.theme.ProgettoMobileTheme
import cohappy.frontend.view.house.HouseProfileView
import cohappy.frontend.viewmodel.RoommateItem

private val mockRoommatesDark = listOf(
    RoommateItem("1", "Alessandro", "Bianchi", isAdmin = true, isMe = true),
    RoommateItem("2", "Marco", "Rossi", isAdmin = false, isMe = false),
    RoommateItem("3", "Sofia", "Verdi", isAdmin = false, isMe = false)
)

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "1. Dark Loading State"
)
@Composable
fun RommateProfileDarkPreviewLoading() {
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

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "2. Dark Normal User State"
)
@Composable
fun RommateProfileDarkPreviewNormal() {
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

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "3. Dark Admin User State"
)
@Composable
fun RommateProfileDarkPreviewAdmin() {
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

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "4. Dark Code Updating State"
)
@Composable
fun RommateProfileDarkPreviewUpdatingCode() {
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

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "5. Dark Code Update Error"
)
@Composable
fun RommateProfileDarkPreviewError() {
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

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "6. Dark Roommates Popup Loading"
)
@Composable
fun RommateProfileDarkPreviewPopupLoading() {
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

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "7. Dark Roommates Popup Populated"
)
@Composable
fun RommateProfileDarkPreviewPopupPopulated() {
    ProgettoMobileTheme {
        HouseProfileView(
            userName = "Alessandro",
            userSurname = "Bianchi",
            imageBytes = null,
            isLoading = false,
            houseAddress = "Via Roma 123, Milano",
            houseCode = "ABC123XYZ",
            showRoommatesPopup = true,
            roommatesList = mockRoommatesDark,
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
