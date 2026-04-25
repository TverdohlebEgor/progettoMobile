package cohappy.frontend.client.dto.response

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
data class ChatMessageDTO(
    var message: String? = null,
    var messageImmage: ByteArray? = null,
    var userCode: String? = null,
    var userImage: ByteArray? = null,
    var timestamp: LocalDateTime? = null
)