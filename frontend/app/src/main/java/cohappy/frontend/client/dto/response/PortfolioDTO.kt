package cohappy.frontend.client.dto.response
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PortfolioDTO(
    var amount: Float? = null,
    var debts: List<DebtDTO>? = null
)