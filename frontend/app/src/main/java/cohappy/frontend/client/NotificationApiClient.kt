package cohappy.frontend.client

import cohappy.frontend.client.dto.NotificationTypeEnum
import cohappy.frontend.client.dto.response.GetNotificationDTO
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApiClient {

    @GET("api/notifications/{userCode}")
    suspend fun getUserNotifications(
        @Path("userCode") userCode: String
    ): Response<List<GetNotificationDTO>>

    @DELETE("api/notifications/{notificationId}")
    suspend fun deleteNotification(
        @Path("notificationId") notificationId: String
    ): Response<String>

    @DELETE("api/notifications/clear/{userCode}")
    suspend fun clearNotifications(
        @Path("userCode") userCode: String,
        @Query("eventType") eventType: NotificationTypeEnum? = null
    ): Response<String>
}