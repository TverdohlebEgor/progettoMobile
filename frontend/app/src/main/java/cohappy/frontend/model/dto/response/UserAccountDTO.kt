package cohappy.frontend.model.dto.response

import java.time.LocalDate
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
data class UserAccountDTO(
    var name: String? = null,
    var surname: String? = null,
    var birthDate: String? = null,
    var cv: String? = null,
    var age: Int? = null,
    var userCode: String? = null, // Mettiamo il nullable per sicurezza
    var images: List<String>? = null, // Da ByteArray a String.
    var email: String? = null,
    var phoneNumber: String? = null,
    var password: String? = null,
    var portfolio: PortfolioDTO? = null
)