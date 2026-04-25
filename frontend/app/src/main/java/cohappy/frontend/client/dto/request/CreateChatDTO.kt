package cohappy.frontend.client.dto.request
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateChatDTO(
    val participating: List<String>,
    val name: String,
    var immage: ByteArray? = null
)