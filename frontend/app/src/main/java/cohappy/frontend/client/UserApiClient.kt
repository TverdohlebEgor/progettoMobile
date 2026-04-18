package cohappy.frontend.client

import cohappy.frontend.model.dto.response.UserAccountDTO
import cohappy.frontend.model.dto.request.LoginDTO
import cohappy.frontend.model.dto.request.RegisterDTO
import retrofit2.Response
import retrofit2.http.*

interface UserApiClient {

    @GET("api/user/profile/{userCode}")
    suspend fun getUserProfile(@Path("userCode") userCode: String): Response<UserAccountDTO>

    @DELETE("api/user/delete/{userCode}")
    suspend fun deleteProfile(@Path("userCode") userCode: String): Response<String>

    @POST("api/user/login")
    suspend fun login(@Body loginDTO: LoginDTO): Response<String>

    @POST("api/user/register")
    suspend fun register(@Body registerDTO: RegisterDTO): Response<String>
}