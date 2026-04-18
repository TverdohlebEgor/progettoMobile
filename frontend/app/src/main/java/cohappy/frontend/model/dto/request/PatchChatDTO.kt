package cohappy.frontend.model.dto.request
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PatchChatDTO(
    val chatCode: String,
    val name: String,
    var immage: ByteArray? = null
)
