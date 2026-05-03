package cohappy.frontend.client.dto.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetNotificationDTO(
    val eventId: String,
    val eventType: String,
    val title: String,
    val subtitle: String,
    val timestamp: String,
    val imageBytes: ByteArray? = null,
    val userCode: String? = null
)