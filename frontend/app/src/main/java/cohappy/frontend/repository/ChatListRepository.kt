package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.CreateChatDTO
import cohappy.frontend.client.dto.response.UserChatDTO
import cohappy.frontend.expections.ErrorMessages.SERVER_ERROR
import cohappy.frontend.expections.ErrorMessages.USER_NOT_FOUND_CREATE_CHAT
import cohappy.frontend.expections.ErrorMessages.USER_NOT_FOUND_GET_CHATS
import cohappy.frontend.expections.NotFoundException
import cohappy.frontend.expections.ServerErrorException
import retrofit2.Response

class ChatListRepository {
    suspend fun createChat(dto: CreateChatDTO): Result<String> {
        try {
            val apiResponse = ClientSingleton.chatApi.createChat(dto)
            if (apiResponse.isSuccessful && apiResponse.body() != null) {
                return Result.success(apiResponse.body()!!)
            } else {
                if (apiResponse.code() == 404) {
                    return Result.failure(NotFoundException(USER_NOT_FOUND_CREATE_CHAT))
                } else {
                    return Result.failure(ServerErrorException(SERVER_ERROR))
                }
            }
        }
        catch (e : Exception){
            return Result.failure(e)
        }
    }

    suspend fun getUserChats(userId: String): Result<List<UserChatDTO>> {
        try {
            val apiResponse = ClientSingleton.chatApi.getUserChats(userId)
            if (apiResponse.isSuccessful && apiResponse.body() != null) {
                return Result.success(apiResponse.body()!!)
            } else {
                if (apiResponse.code() == 404) {
                    return Result.failure(NotFoundException(USER_NOT_FOUND_GET_CHATS))
                } else {
                    return Result.failure(ServerErrorException(SERVER_ERROR))
                }
            }
        }
        catch (e : Exception){
            return Result.failure(e)
        }
    }
}