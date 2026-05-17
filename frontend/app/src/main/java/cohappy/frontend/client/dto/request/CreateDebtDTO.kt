package cohappy.frontend.client.dto.request
import cohappy.frontend.client.dto.enum.DebtType
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateDebtDTO(
    val creditorCode: String,
    val receiverCode: Map<String,Boolean>,
    val amount: Float,
    var description: String? = null,
    val debtType: DebtType
)