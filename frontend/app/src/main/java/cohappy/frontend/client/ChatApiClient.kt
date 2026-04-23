package cohappy.frontend.client

import cohappy.frontend.model.dto.response.ChatMessageDTO
import cohappy.frontend.model.dto.response.GetChatDTO
import cohappy.frontend.model.dto.response.UserChatDTO
import cohappy.frontend.model.dto.request.*
import retrofit2.Response
import retrofit2.http.*

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