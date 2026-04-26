package cohappy.frontend.repository;

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.AddMessageDTO
import cohappy.frontend.client.dto.request.CreateChatDTO

class SingleChatRepository {

    suspend fun getUserProfile(userId: String) =
        ClientSingleton.userApi.getUserProfile(userId)

    suspend fun getUserChats(userId: String) =
        ClientSingleton.chatApi.getUserChats(userId)

    suspend fun createChat(dto: CreateChatDTO) =
        ClientSingleton.chatApi.createChat(dto)

    suspend fun getHouseAdvertisements() =
        ClientSingleton.houseApi.getAllHouseAdvertisements()

    suspend fun getMessages(chatId: String) =
        ClientSingleton.chatApi.getMessages(chatId)

    suspend fun sendMessage(dto: AddMessageDTO): Result<Unit> {
        return try {
            val response = ClientSingleton.chatApi.addMessage(dto)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Errore invio messaggio: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}