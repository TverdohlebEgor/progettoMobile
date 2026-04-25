package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.RegisterDTO

class RegistrationRepository {
    suspend fun registerUser(dto: RegisterDTO): Result<Unit> {
        return try {
            val risposta = ClientSingleton.userApi.register(dto).toString()

            if (risposta.contains("409") ||
                risposta.contains("422") ||
                risposta.contains("duplicate key error")
            ) {
                Result.failure(Exception("Utente già presente nel database"))
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}