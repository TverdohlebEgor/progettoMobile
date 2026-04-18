package cohappy.frontend.model.dto.response

import java.time.LocalDateTime
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatMessageDTO(
    var message: String? = null,
    var messageImmage: ByteArray? = null,
    var userCode: String? = null,
    var userImage: ByteArray? = null,
    var timestamp: LocalDateTime? = null
)