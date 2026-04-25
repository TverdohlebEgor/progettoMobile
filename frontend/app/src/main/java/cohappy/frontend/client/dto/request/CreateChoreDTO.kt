package cohappy.frontend.client.dto.request

import java.time.LocalDate

data class CreateChoreDTO(
    var days: List<LocalDate>? = null,
    var assignedTo: Map<LocalDate, String>? = null,
    var createdBy: String? = null,
    var houseCode: String? = null,
    var name: String? = null,
    var description: String? = null
)
