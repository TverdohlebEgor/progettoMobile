package cohappy.frontend.client.dto.response
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetChatDTO(
    var chatCode: String? = null,
    var participating: List<String>? = null,
    var name: String? = null,
    var immage: ByteArray? = null
)