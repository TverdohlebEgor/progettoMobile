package cohappy.frontend.client.dto.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterDTO(
    val name: String,
    val surname: String,
    val birthDate: String,
    var images: List<ByteArray> = emptyList(),
    val email: String,
    val phoneNumber: String,
    val password: String
)
