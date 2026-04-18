package cohappy.frontend.model.dto.request
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddMessageDTO(
    val chatCode: String,
    val message: String,
    var messageImmage: ByteArray? = null,
    val userCode: String
)
