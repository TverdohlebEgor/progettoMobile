package cohappy.frontend.model.dto.request

import cohappy.frontend.model.dto.HouseStateEnum
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModifyHouseAdvertisementDTO(
    val houseCode: String,
    val state: HouseStateEnum,
    var description: String? = null
)
