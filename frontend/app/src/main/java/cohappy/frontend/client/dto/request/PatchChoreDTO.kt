package cohappy.frontend.client.dto.request

import com.squareup.moshi.JsonClass
import java.time.LocalDate

@JsonClass(generateAdapter = true)
data class PatchChoreDTO(
    val choreCode: String,
    val day: LocalDate? = null,
    var assignedTo: String? = null,
    var completed: Boolean? = null,
    var houseCode: String? = null,
    var name: String? = null,
    var description: String? = null
)
