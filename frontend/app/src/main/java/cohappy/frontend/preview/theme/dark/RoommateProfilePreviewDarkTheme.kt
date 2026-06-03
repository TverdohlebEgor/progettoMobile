package cohappy.frontend.preview.theme.dark

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.ui.theme.ProgettoMobileTheme
import cohappy.frontend.view.house.HouseProfileView
import cohappy.frontend.viewmodel.RoommateItem

private val mockRoommatesDark = listOf(
    RoommateItem(userCode = "1", name = "Ale", surname = "Rossi", isAdmin = true, isMe = true),
    RoommateItem(
        userCode = "2",
        name = "Sofia",
        surname = "Bianchi",
        isAdmin = false,
        isMe = false
    ),
    RoommateItem(userCode = "3", name = "Marco", surname = "Verdi", isAdmin = false, isMe = false)
)

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "1. Dark Loading State")
@Composable
fun RoommateProfileDarkPreviewLoading() {
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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "2. Dark Normal User State")
@Composable
fun RoommateProfileDarkPreviewNormal() {
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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "3. Dark Update Code Error")
@Composable
fun RoommateProfileDarkPreviewCodeError() {
    ProgettoMobileTheme {
        HouseProfileView(
            userName = "Ale",
            userSurname = "Rossi",
            imageBytes = null,
            isLoading = false,
            houseAddress = "Via Garibaldi 12",
            houseCode = "COH-1234",
            hasExistingAd = false
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "4. Dark Roommates Loading Popup")
@Composable
fun RoommateProfileDarkPreviewRoommatesLoading() {
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

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "5. Dark Roommates Loaded (Admin)")
@Composable
fun RoommateProfileDarkPreviewRoommatesLoaded() {
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
            roommatesList = mockRoommatesDark,
            isCurrentUserAdmin = true,
            hasExistingAd = true
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "6. Dark Admin View - Edit Advertisement")
@Composable
fun RoommateProfileDarkPreviewAdminEditAd() {
    ProgettoMobileTheme {
        HouseProfileView(
            userName = "Ale",
            userSurname = "Rossi",
            imageBytes = null,
            isLoading = false,
            houseAddress = "Via Garibaldi 12",
            houseCode = "COH-1234",
            isCurrentUserAdmin = true,
            hasExistingAd = true
        )
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "7. Dark Roommate Actions (Admin Buttons)")
@Composable
fun RoommateProfileDarkPreviewAdminActions() {
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
            roommatesList = mockRoommatesDark,
            isCurrentUserAdmin = true,
            hasExistingAd = true
        )
    }
}