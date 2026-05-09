package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.AddMessageDTO
import cohappy.frontend.client.dto.response.ChatMessageDTO
import cohappy.frontend.expections.ErrorMessages.CHAT_NOT_FOUND
import cohappy.frontend.expections.ErrorMessages.SERVER_ERROR
import cohappy.frontend.expections.NotFoundException
import cohappy.frontend.expections.ServerErrorException

class SingleChatRepository {
    suspend fun getMessages(chatId: String): Result<List<ChatMessageDTO>> {
        return try {
            val response = ClientSingleton.chatApi.getMessages(chatId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                when (response.code()) {
                    404 -> Result.failure(NotFoundException(CHAT_NOT_FOUND))
                    else -> Result.failure(ServerErrorException(SERVER_ERROR))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(dto: AddMessageDTO): Result<Unit> {
        return try {
            val response = ClientSingleton.chatApi.addMessage(dto)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                when (response.code()) {
                    404 -> Result.failure(NotFoundException(CHAT_NOT_FOUND))
                    else -> Result.failure(ServerErrorException(SERVER_ERROR))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
