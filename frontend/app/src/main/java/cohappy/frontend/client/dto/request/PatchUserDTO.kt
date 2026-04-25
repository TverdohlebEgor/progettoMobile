package cohappy.frontend.client.dto.request

import com.squareup.moshi.JsonClass
import java.time.LocalDate

@JsonClass(generateAdapter = true)
data class PatchUserDTO(
    val userCode: String,
    var name: String? = null,
    var surname: String? = null,
    var birthDate: LocalDate? = null,
    var age: Int? = null,
    var images: List<ByteArray>? = null,
    var email: String? = null,
    var phoneNumber: String? = null,
    var password: String? = null
)