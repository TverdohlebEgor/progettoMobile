package cohappy.frontend.client.dto.response

import cohappy.frontend.client.dto.HouseStateEnum
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetHouseAdvertesimentDTO(
    var houseCode: String? = null,
    var images: List<ByteArray>? = null,
    var costPerMonth: Int? = null,
    var country: String? = null,
    var region: String? = null,
    var street: String? = null,
    var civicNumber: Int? = null,
    var state: HouseStateEnum? = null,
    var publishedByCode: String? = null,
    var publishedByImages: List<ByteArray>? = null,
    var publishedByEmail: String? = null,
    var publishedByPhoneNumber: String? = null,
    var description: String? = null
)