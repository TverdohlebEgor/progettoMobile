package cohappy.frontend.model.dto.response;
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetHouseDTO(
    var houseCode: String? = null,
    var admins: List<String>? = null,
    var users: List<String>? = null,
    var images: List<ByteArray>? = null,
    var costPerMonth: Int? = null,
    var country: String? = null,
    var region: String? = null,
    var street: String? = null,
    var civicNumber: Int? = null
)