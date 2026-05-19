package cohappy.frontend.client.dto.response
import cohappy.frontend.client.dto.enum.DebtType
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DebtDTO(
    var debtId: String? = null,
    var linkedDebtId: String? = null,
    var creditorUserCode: String? = null,
    var debtorsUserCode: Map<String,Boolean>? = null,
    var isCreatorIncluded: Boolean? = null,
    var amount: Float? = null,
    var description: String? = null,
    val debtType: DebtType
)