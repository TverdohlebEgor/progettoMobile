package cohappy.frontend.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.LoginDTO

class LoginRepository(
    private val sharedPref: SharedPreferences
) {
    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val pacchettoLogin = LoginDTO(email = email, password = password)
            val apiResponse = ClientSingleton.userApi.login(pacchettoLogin)

            if (apiResponse.isSuccessful) {
                val token = apiResponse.body() ?: ""
                sharedPref.edit { putString("USER_TOKEN", token) }
                Result.success(token)
            } else {
                Result.failure(Exception("Credenziali errate"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}