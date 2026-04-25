package cohappy.frontend.client.dto.request
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateDebtDTO(
    val senderUserCode: String,
    val receiverUserCode: String,
    val amount: Float,
    var description: String? = null,
)