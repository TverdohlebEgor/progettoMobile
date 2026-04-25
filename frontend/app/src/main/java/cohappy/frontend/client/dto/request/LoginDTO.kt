package cohappy.frontend.client.dto.request
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginDTO(
    val email: String? = null,
    val phoneNumber: String? = null,
    val password: String
)
