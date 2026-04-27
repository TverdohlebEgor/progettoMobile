package cohappy.frontend.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import cohappy.frontend.model.ChatListViewModel
import cohappy.frontend.view.chat.ChatListView

@Composable
fun ChatListScreen(
    userToken: String?,
    onChatClick: (String) -> Unit,
    viewModel: ChatListViewModel = viewModel()
) {
    LaunchedEffect(userToken) {
        viewModel.loadChats(userToken)
    }

    ChatListView(
        isLoading = viewModel.isLoading,
        searchQuery = viewModel.searchQuery,
        filteredChats = viewModel.getFilteredChats(),
        onSearchChange = { nuovaQuery -> viewModel.updateSearchQuery(nuovaQuery) },
        onChatClick = onChatClick
    )
}