package cohappy.frontend.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.view.ad.AdListView
import cohappy.frontend.viewmodel.AdListViewModel

@Composable
fun AdListScreen(
    onAdClick: (String) -> Unit,
    viewModel: AdListViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadAdvertisements()
    }

    AdListView(
        isLoading = viewModel.isLoading,
        searchQuery = viewModel.searchQuery,
        filteredAds = viewModel.getFilteredAds(),
        onSearchChange = { nuovaQuery -> viewModel.updateSearchQuery(nuovaQuery) },
        onAdClick = onAdClick
    )
}