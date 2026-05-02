package cohappy.frontend.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.view.house.HouseProfileView

@Preview(showBackground = true)
@Composable
fun PreviewHouseProfile() {
    MaterialTheme {
        HouseProfileView(
            userName = "Sofia",
            userSurname = "Rossi",
            imageBytes = null,
            isLoading = false,
            onLeaveHouseClick = {},
            onLogoutClick = {},
            onRulesClick = {},
            onRoommatesClick = {},
            onPasswordChangeClick = {}
        )
    }
}