package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.CreateChatDTO
import cohappy.frontend.client.dto.response.UserChatDTO
import retrofit2.Response

class ChatListRepository {
    suspend fun createChat(dto: CreateChatDTO) =
        ClientSingleton.chatApi.createChat(dto)
    suspend fun getUserChats(userId: String) : Response<List<UserChatDTO>>
        = ClientSingleton.chatApi.getUserChats(userId)
}