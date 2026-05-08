package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.AddUserDTO
import cohappy.frontend.client.dto.request.PatchUserDTO
import cohappy.frontend.client.dto.response.UserAccountDTO
import cohappy.frontend.expections.ErrorMessages.SERVER_ERROR
import cohappy.frontend.expections.ServerErrorException
import retrofit2.Response

class UserProfileRepository {

    suspend fun fetchUserProfile(userCode: String): Result<UserAccountDTO> {
        return try {
            val response = ClientSingleton.userApi.getUserProfile(userCode)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(ServerErrorException(SERVER_ERROR))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserImage(userCode: String, imageBytes: ByteArray): Result<String> {
        return try {
            val patchRequest = PatchUserDTO(
                userCode = userCode,
                images = listOf(imageBytes)
            )
            val response = ClientSingleton.userApi.patchUser(patchRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(ServerErrorException(SERVER_ERROR))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinHouse(houseCode: String, userCode: String): Result<String> {
        return try {
            val pacchetto = AddUserDTO(houseCode = houseCode, userCode = userCode)
            val response = ClientSingleton.houseApi.addUser(pacchetto)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(ServerErrorException(SERVER_ERROR))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}