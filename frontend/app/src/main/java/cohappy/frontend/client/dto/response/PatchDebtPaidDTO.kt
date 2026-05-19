package cohappy.frontend.client.dto.response
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PatchDebtPaidDTO (
    var debtId: String? = null,
    var receiverCode: String? = null,
    var newState: Boolean? = null
)