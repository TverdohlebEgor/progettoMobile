package cohappy.frontend.model.dto.request

import cohappy.frontend.model.dto.MoveMoneyOperationEnum
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MoveMoneyDTO(
    val userCode: String,
    val operation: MoveMoneyOperationEnum,
    val amount: Float
)

