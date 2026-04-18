package cohappy.frontend.model.dto.request
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddAdminDTO(
    val houseCode: String,
    val userCode: String
)
