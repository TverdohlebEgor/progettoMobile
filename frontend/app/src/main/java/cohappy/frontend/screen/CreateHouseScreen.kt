package cohappy.frontend.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.view.ad.CreateHouseView
import cohappy.frontend.viewmodel.CreateHouseViewModel

@Composable
fun CreateHouseScreen(
    userToken: String,
    onBackClick: () -> Unit,
    onHouseCreated: (String) -> Unit,
    viewModel: CreateHouseViewModel = viewModel()
) {
    LaunchedEffect(viewModel.creationSuccess) {
        if (viewModel.creationSuccess) {
            val code = viewModel.createdHouseCode
            viewModel.resetSuccess()
            onHouseCreated(code)
        }
    }

    CreateHouseView(
        province = viewModel.province,
        city = viewModel.city,
        street = viewModel.street,
        civicNumber = viewModel.civicNumber,
        isLoading = viewModel.isLoading,
        errorMessage = viewModel.errorMessage,
        onProvinceChange = { viewModel.updateProvince(it) },
        onCityChange = { viewModel.updateCity(it) },
        onStreetChange = { viewModel.updateStreet(it) },
        onCivicChange = { viewModel.updateCivicNumber(it) },
        onCreateClick = { viewModel.createHouse(userToken) },
        onBackClick = onBackClick
    )
}