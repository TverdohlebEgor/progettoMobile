package cohappy.frontend.client.dto.request

import cohappy.frontend.client.dto.MoveMoneyOperationEnum
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MoveMoneyDTO(
    val userCode: String,
    val operation: MoveMoneyOperationEnum,
    val amount: Float
)

