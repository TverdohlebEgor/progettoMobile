package cohappy.frontend.model.dto.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterDTO(
    val name: String,
    val surname: String,
    val birthDate: String,
    val cf: String,
    var images: List<ByteArray>? = null,
    val email: String,
    val phoneNumber: String,
    val password: String
)
