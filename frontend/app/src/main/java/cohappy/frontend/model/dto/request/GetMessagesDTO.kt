package cohappy.frontend.model.dto.request
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetMessagesDTO(
    var startProgressive: Integer? = null,
    var endProgressive: Integer? = null
)
