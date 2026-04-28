package cohappy.frontend.client.dto.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserAccountDTO(
    var name: String? = null,
    var surname: String? = null,
    var birthDate: String? = null,
    var cv: String? = null,
    var age: Int? = null,
    var userCode: String? = null, 
    var images: List<ByteArray>? = null,
    var email: String? = null,
    var phoneNumber: String? = null,
    var password: String? = null,
    var portfolio: PortfolioDTO? = null
)