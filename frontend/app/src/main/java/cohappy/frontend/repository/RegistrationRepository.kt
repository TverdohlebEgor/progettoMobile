package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.RegisterDTO
import cohappy.frontend.expections.ErrorMessages.ALREADY_USED_CREDENTIAL_REGISTRATION
import cohappy.frontend.expections.ErrorMessages.SERVER_ERROR
import cohappy.frontend.expections.ServerErrorException

class RegistrationRepository {
    suspend fun registerUser(dto: RegisterDTO): Result<String> {
        return try {
            val apiResponse = ClientSingleton.userApi.register(dto)
            val userCode = apiResponse.body() ?: ""
            val userCodePulito = userCode.replace("\"", "").trim()
            if (apiResponse.isSuccessful) {
                Result.success(userCodePulito)
            } else {
                if(apiResponse.code() == 400){
                    Result.failure(Exception(ALREADY_USED_CREDENTIAL_REGISTRATION))
                } else {
                    Result.failure(ServerErrorException(SERVER_ERROR))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}