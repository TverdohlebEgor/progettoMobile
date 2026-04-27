package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.response.UserChatDTO
import retrofit2.Response

class ChatListRepository {
    suspend fun fetchUserChats(userCode: String): Response<List<UserChatDTO>> {
        return ClientSingleton.chatApi.getUserChats(userCode)
    }
}