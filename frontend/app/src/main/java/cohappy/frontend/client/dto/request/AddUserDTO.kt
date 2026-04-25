package cohappy.frontend.client.dto.request
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddUserDTO(
    val houseCode: String,
    val userCode: String
)
