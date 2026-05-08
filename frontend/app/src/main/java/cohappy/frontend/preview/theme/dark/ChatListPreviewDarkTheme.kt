package cohappy.frontend.preview.theme.dark

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.expections.ErrorMessages.SERVER_ERROR
import cohappy.frontend.expections.ErrorMessages.USER_NOT_FOUND_GET_CHATS
import cohappy.frontend.util.randomPhoto
import cohappy.frontend.view.chat.ChatListView
import cohappy.frontend.viewmodel.ChatListItem

@Preview(showBackground = true, name = "Chat List - Empty", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewChatListEmpty() {
    ChatListView(
        isLoading = false,
        searchQuery = "",
        isError = false,
        errorMessage = "",
        filteredChats = emptyList(),
        onSearchChange = {},
        onChatClick = {}
    )
}

@Preview(showBackground = true, name = "Chat List - Success", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewChatList() {
    ChatListView(
        isLoading = false,
        searchQuery = "",
        isError = false,
        errorMessage = "",
        filteredChats = listOf(
            ChatListItem(
                "chat1",
                "Egor",
                "Birra?",
                "10:00",
                null
            ),
            ChatListItem(
                "chat2",
                "PhotoMan",
                "I'm a photo",
                "12:00",
                randomPhoto(LocalContext.current)
            )
        ),
        onSearchChange = {},
        onChatClick = {}
    )
}

@Preview(showBackground = true, name = "Chat List - Loading", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewChatListLoading() {
    ChatListView(
        isLoading = true,
        searchQuery = "",
        isError = false,
        errorMessage = "",
        filteredChats = emptyList(),
        onSearchChange = {},
        onChatClick = {}
    )
}

@Preview(showBackground = true, name = "Chat List - Error (Server)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewChatListErrorServer() {
    ChatListView(
        isLoading = false,
        searchQuery = "",
        isError = true,
        errorMessage = SERVER_ERROR,
        filteredChats = emptyList(),
        onSearchChange = {},
        onChatClick = {},
        onRetry = {}
    )
}

@Preview(showBackground = true, name = "Chat List - Error (User Not Found)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewChatListErrorUserNotFound() {
    ChatListView(
        isLoading = false,
        searchQuery = "",
        isError = true,
        errorMessage = USER_NOT_FOUND_GET_CHATS,
        filteredChats = emptyList(),
        onSearchChange = {},
        onChatClick = {},
        onRetry = {}
    )
}

@Preview(showBackground = true, name = "Chat List - Error (Network)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewChatListErrorGeneric() {
    ChatListView(
        isLoading = false,
        searchQuery = "",
        isError = true,
        errorMessage = "Unable to resolve host \"api.cohappy.com\": No address associated with hostname",
        filteredChats = emptyList(),
        onSearchChange = {},
        onChatClick = {},
        onRetry = {}
    )
}
