package cohappy.frontend.client.dto.request
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateHouseDTO(
    val userCode: String,
    val costPerMonth: Int?=null, //INTEGER
    val country: String,
    val region: String,
    val street: String,
    val civicNumber: Int //INTEGER
)
