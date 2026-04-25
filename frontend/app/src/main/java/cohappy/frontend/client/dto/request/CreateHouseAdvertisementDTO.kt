package cohappy.frontend.client.dto.request

import cohappy.frontend.client.dto.HouseStateEnum
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateHouseAdvertisementDTO(
    val houseCode: String,
    val state: HouseStateEnum,
    val publishedBy: String,
    var description: String? = null
)
