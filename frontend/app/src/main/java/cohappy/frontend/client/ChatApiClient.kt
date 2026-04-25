package cohappy.frontend.client

import cohappy.frontend.client.dto.request.AddMessageDTO
import cohappy.frontend.client.dto.request.CreateChatDTO
import cohappy.frontend.client.dto.request.PatchChatDTO
import cohappy.frontend.client.dto.request.PatchChatUsersDTO
import cohappy.frontend.client.dto.response.ChatMessageDTO
import cohappy.frontend.client.dto.response.GetChatDTO
import cohappy.frontend.client.dto.response.UserChatDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApiClient {

    @GET("api/chat/user/{userCode}")
    suspend fun getUserChats(@Path("userCode") userCode: String?): Response<List<UserChatDTO>>

    @GET("api/chat/{chatCode}")
    suspend fun getChat(@Path("chatCode") chatCode: String): Response<GetChatDTO>

    @GET("api/chat/messages/{chatCode}")
    suspend fun getMessages(
        @Path("chatCode") chatCode: String,
        //@QueryMap options: Map<String, String> // Supports the fields in GetMessagesDTO
    ): Response<List<ChatMessageDTO>>

    @POST("api/chat/create")
    suspend fun createChat(@Body request: CreateChatDTO): Response<String>

    @PATCH("api/chat/patch")
    suspend fun patchChat(@Body request: PatchChatDTO): Response<String>

    @PATCH("api/chat/patch/user")
    suspend fun patchChatUsers(@Body request: PatchChatUsersDTO): Response<String>

    @POST("api/chat/message/add")
    suspend fun addMessage(@Body request: AddMessageDTO): Response<String>
}