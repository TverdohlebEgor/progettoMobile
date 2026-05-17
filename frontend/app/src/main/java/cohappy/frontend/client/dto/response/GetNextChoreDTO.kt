package cohappy.frontend.client.dto.response;

import com.squareup.moshi.JsonClass
import java.time.LocalDate

@JsonClass(generateAdapter = true)
data class GetNextChoreDTO(
    val choreCode : String,
    val name: String,
    val assignedTo: String,
    val date: LocalDate,
    val completed: Boolean) {
}
