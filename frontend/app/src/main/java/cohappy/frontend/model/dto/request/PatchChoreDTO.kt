package cohappy.frontend.model.dto.request;

import java.time.LocalDate;

data class PatchChoreDTO(
    val choreCode: String,
    val day: LocalDate,
    var assignedTo: String? = null,
    var completed: Boolean? = null,
    var houseCode: String? = null,
    var name: String? = null,
    var description: String? = null
)
