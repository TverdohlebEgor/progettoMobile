package cohappy.frontend.model.dto.request

import cohappy.frontend.model.dto.PatchChatUsersOperationEnum
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PatchChatUsersDTO(
    val chatCode: String,
    var usersCode: List<String>? = null,
    val operation: PatchChatUsersOperationEnum
)
