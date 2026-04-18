package cohappy.frontend.model.dto.request
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterDTO(
    var images: List<ByteArray>? = null,
    val email: String,
    val phoneNumber: String,
    val password: String
)
