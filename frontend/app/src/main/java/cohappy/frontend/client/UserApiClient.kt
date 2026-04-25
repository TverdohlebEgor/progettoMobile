package cohappy.frontend.client

import cohappy.frontend.client.dto.request.LoginDTO
import cohappy.frontend.client.dto.request.PatchUserDTO
import cohappy.frontend.client.dto.request.RegisterDTO
import cohappy.frontend.client.dto.response.UserAccountDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApiClient {

    @GET("api/user/profile/{userCode}")
    suspend fun getUserProfile(@Path("userCode") userCode: String): Response<UserAccountDTO>

    @DELETE("api/user/delete/{userCode}")
    suspend fun deleteProfile(@Path("userCode") userCode: String): Response<String>

    @POST("api/user/login")
    suspend fun login(@Body loginDTO: LoginDTO): Response<String>

    @POST("api/user/register")
    suspend fun register(@Body registerDTO: RegisterDTO): Response<String>

    @PATCH("api/user/patch")
    suspend fun patchUser(@Body patchUserDTO: PatchUserDTO): Response<String>
}