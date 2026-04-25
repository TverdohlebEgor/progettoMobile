package cohappy.frontend.client.dto.request

import com.squareup.moshi.JsonClass

// ... existing code ...

@JsonClass(generateAdapter = true)
data class RegisterDTO(
    val name: String,
    val surname: String,
    val birthDate: String,
    var images: List<ByteArray> = emptyList(), // 💅 MAGIC: Niente più null! Di base è una lista vuota []
    val email: String,
    val phoneNumber: String,
    val password: String
)
