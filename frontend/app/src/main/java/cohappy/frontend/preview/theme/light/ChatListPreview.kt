package cohappy.frontend.preview.theme.light

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import cohappy.frontend.util.randomPhoto
import cohappy.frontend.view.auth.LoginView
import cohappy.frontend.view.chat.ChatListView
import cohappy.frontend.viewmodel.ChatListItem

@Preview(showBackground = true, name = "Login - Standard")
@Composable
fun PreviewChatListEmpty() {
    ChatListView(
        false,
        "",
        emptyList(),
        {},
        {}
    )
}

@Preview(showBackground = true, name = "Login - Standard")
@Composable
fun PreviewChatList() {
    ChatListView(
        false,
        "",
        listOf(
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
        {},
        {}
    )
}

@Preview(showBackground = true, name = "Login - Standard")
@Composable
fun PreviewChatListWithSearchQuery() {
    ChatListView(
        false,
        "Egor",
        listOf(
            ChatListItem(
                "chat1",
                "Egor",
                "Birra?",
                "10:00",
                null
            )
        ),
        {},
        {}
    )
}

@Preview(showBackground = true, name = "Login - Standard")
@Composable
fun PreviewChatListLoading() {
    ChatListView(
        true,
        "Egor",
        emptyList(),
        {},
        {}
    )
}