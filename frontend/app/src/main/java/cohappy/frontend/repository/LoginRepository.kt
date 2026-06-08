package cohappy.frontend.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.LoginDTO
import cohappy.frontend.expections.ErrorMessages.SERVER_ERROR
import cohappy.frontend.expections.ErrorMessages.WRONG_CREDENTIAL_LOGIN
import cohappy.frontend.expections.NotFoundException
import cohappy.frontend.expections.ServerErrorException

class LoginRepository(
    private val sharedPref: SharedPreferences
) {
    suspend fun login(email: String, password: String): Result<String> {
        try {
            val pacchettoLogin = LoginDTO(email = email, password = password)
            val apiResponse = ClientSingleton.userApi.login(pacchettoLogin)

            if (apiResponse.isSuccessful) {
                val userCode = apiResponse.body() ?: ""
                val userCodePulito = userCode.replace("\"", "").trim()
                sharedPref.edit {
                    putString("USER_TOKEN", userCodePulito)
                }

                // Carica il profilo per vedere se è già in una casa
                val profileResponse = ClientSingleton.userApi.getUserProfile(userCodePulito)
                if (profileResponse.isSuccessful) {
                    val profile = profileResponse.body()
                    if (profile?.houseCode != null) {
                        sharedPref.edit {
                            putString("HOUSE_CODE", profile.houseCode)
                        }
                    }
                }

                return Result.success(userCodePulito)
            } else {
                if (apiResponse.code() == 404) {
                    return Result.failure(NotFoundException(WRONG_CREDENTIAL_LOGIN))
                } else {
                    return Result.failure(ServerErrorException(SERVER_ERROR))
                }
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}