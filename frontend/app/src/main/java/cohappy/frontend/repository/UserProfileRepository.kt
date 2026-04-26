package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.PatchUserDTO
import cohappy.frontend.client.dto.response.UserAccountDTO
import retrofit2.Response

class UserProfileRepository {

    suspend fun fetchUserProfile(userCode: String): Response<UserAccountDTO> {
        return ClientSingleton.userApi.getUserProfile(userCode)
    }

    suspend fun updateUserImage(userCode: String, imageBytes: ByteArray): Response<String> {
        val patchRequest = PatchUserDTO(
            userCode = userCode,
            images = listOf(imageBytes)
        )
        return ClientSingleton.userApi.patchUser(patchRequest)
    }
}