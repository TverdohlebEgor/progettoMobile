package cohappy.frontend.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.model.AdListViewModel
import cohappy.frontend.view.Ad.AdListView

@Composable
fun AdListScreen(
    innerPadding: PaddingValues,
    onAdClick: (String) -> Unit,
    viewModel: AdListViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadAdvertisements()
    }

    AdListView(
        innerPadding = innerPadding,
        isLoading = viewModel.isLoading,
        searchQuery = viewModel.searchQuery,
        filteredAds = viewModel.getFilteredAds(),
        onSearchChange = { nuovaQuery -> viewModel.updateSearchQuery(nuovaQuery) },
        onAdClick = onAdClick
    )
}