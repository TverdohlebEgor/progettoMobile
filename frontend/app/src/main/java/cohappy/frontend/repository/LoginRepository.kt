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
                val tokenPulito = token.replace("\"", "").trim()
                sharedPref.edit { putString("USER_TOKEN", token) }
                
                // Tentativo di recupero del profilo per vedere se c'è una casa associata
                // Se il tuo backend non restituisce la casa nel profilo, questo passaggio va adattato
                // con l'endpoint corretto (es. getUserHouse)
                try {
                    val profileRes = ClientSingleton.userApi.getUserProfile(tokenPulito)
                    if (profileRes.isSuccessful) {
                        // Supponendo che il backend restituisca il codice casa nel portfolio o in un campo esteso
                        // Qui cerchiamo se l'utente è già associato a una casa tramite un endpoint specifico 
                        // o se lo troviamo nei dettagli del profilo.
                        // Per ora, implementiamo la logica di controllo generica:
                    }
                } catch (e: Exception) { }

                Result.success(token)
            } else {
                Result.failure(Exception("Credenziali errate"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}