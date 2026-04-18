package cohappy.frontend.model.dto.response
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DebtDTO(
    var debtId: String? = null,
    var linkedDebtId: String? = null,
    var debtorUserCode: String? = null,
    var beneficiaryUserCode: String? = null,
    var amount: Float? = null,
    var description: String? = null
)