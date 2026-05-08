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

class ChatListViewModel(
    private val repository: ChatListRepository = ChatListRepository()
) : ViewModel() {
    var chatsList by mutableStateOf<List<ChatListItem>>(emptyList())
        private set
    var isLoading by mutableStateOf(true)
        private set
    var searchQuery by mutableStateOf("")
        private set

    var isError by mutableStateOf(false)
    var errorMessage by mutableStateOf("")
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

                val result = repository.getUserChats(cleanToken)

                if (result.isSuccess) {
                    chatsList = result.getOrNull()?.map { dto ->
                        ChatListItem(
                            id = dto.chatCode ?: "",
                            name = dto.name ?: "Chat",
                            lastMessage = dto.lastMessage ?: "Tocca per aprire...",
                            time = "",
                            image = dto.image
                        )
                    } ?: emptyList()
                } else {
                    chatsList = emptyList()
                    errorMessage = result.exceptionOrNull()?.message ?: "Errore sconosciuto"
                    isError = true
                }
            } catch (e: Exception) {
                chatsList = emptyList()
                errorMessage = e.message ?: "Errore sconosciuto"
                isError = true
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