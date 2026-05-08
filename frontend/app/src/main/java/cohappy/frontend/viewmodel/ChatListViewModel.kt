package cohappy.frontend.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cohappy.frontend.repository.ChatListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatListItem(
    val id: String,
    val name: String,
    val lastMessage: String,
    val time: String,
    val image: ByteArray?
)

class ChatListViewModel : ViewModel() {
    private val repository = ChatListRepository()
    var chatsList by mutableStateOf<List<ChatListItem>>(emptyList())
        private set
    var isLoading by mutableStateOf(true)
        private set
    var searchQuery by mutableStateOf("")
        private set
    fun loadChats(userToken: String?) {
        viewModelScope.launch {
            isLoading = true
            try {
                val cleanToken = userToken?.replace("\"", "")?.trim() ?: ""
                if (cleanToken.isBlank()) {
                    chatsList = emptyList()
                    return@launch
                }

                val result = withContext(Dispatchers.IO) {
                    repository.getUserChats(cleanToken)
                }

                if (result.isSuccess) {
                    chatsList = result.getOrNull()?.map { dto ->
                        ChatListItem(
                            id = dto.chatCode ?: "",
                            name = dto.name ?: "Chat",
                            lastMessage = "Tocca per aprire...",
                            time = "",
                            image = dto.image
                        )
                    } ?: emptyList()
                } else {
                    chatsList = emptyList()
                }
            } catch (_: Exception) {
                chatsList = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun getFilteredChats(): List<ChatListItem> {
        return if (searchQuery.isBlank()) {
            chatsList
        } else {
            chatsList.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.lastMessage.contains(searchQuery, ignoreCase = true)
            }
        }
    }
}