package cohappy.frontend.model.dto.response;

data class GetChoreDTO(
    val choreCode: String? = null,
    var assignedTo: String? = null,
    var assignedToName: String? = null,
    var assignedToImage: ByteArray? = null,
    var completed: Boolean = false,
    var createdBy: String? = null,
    var houseCode: String? = null,
    var name: String? = null,
    var description: String? = null
)
