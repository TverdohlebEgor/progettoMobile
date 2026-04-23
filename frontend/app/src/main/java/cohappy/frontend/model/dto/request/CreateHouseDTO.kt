package cohappy.frontend.model.dto.request
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateHouseDTO(
    val userCode: String,
    var images: List<ByteArray>? = null,
    val costPerMonth: Int, //INTEGER
    val country: String,
    val region: String,
    val street: String,
    val civicNumber: Int //INTEGER
)
