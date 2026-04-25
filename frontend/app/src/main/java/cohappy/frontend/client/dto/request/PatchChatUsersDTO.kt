package cohappy.frontend.client.dto.request

import cohappy.frontend.client.dto.PatchChatUsersOperationEnum
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PatchChatUsersDTO(
    val chatCode: String,
    var usersCode: List<String>? = null,
    val operation: PatchChatUsersOperationEnum
)
