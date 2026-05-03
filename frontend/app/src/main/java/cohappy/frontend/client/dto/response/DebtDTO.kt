package cohappy.frontend.client.dto.response
import cohappy.frontend.client.dto.DebtType
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DebtDTO(
    var debtId: String? = null,
    var linkedDebtId: String? = null,
    var debtorUserCode: String? = null,
    var beneficiaryUserCode: String? = null,
    var amount: Float? = null,
    var description: String? = null,
    val debtType: DebtType
)