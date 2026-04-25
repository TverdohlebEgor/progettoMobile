package cohappy.frontend.client

import cohappy.frontend.client.dto.request.AddAdminDTO
import cohappy.frontend.client.dto.request.AddUserDTO
import cohappy.frontend.client.dto.request.CreateHouseAdvertisementDTO
import cohappy.frontend.client.dto.request.CreateHouseDTO
import cohappy.frontend.client.dto.request.ModifyHouseAdvertisementDTO
import cohappy.frontend.client.dto.request.ModifyHouseDTO
import cohappy.frontend.client.dto.request.RemoveAdminDTO
import cohappy.frontend.client.dto.request.RemoveUserDTO
import cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO
import cohappy.frontend.client.dto.response.GetHouseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface HouseApiClient {

    // --- Advertisements ---
    @GET("api/house/advertisement/all")
    suspend fun getAllHouseAdvertisements(): Response<List<GetHouseAdvertesimentDTO>>

    @GET("api/house/advertisement/{houseCode}")
    suspend fun getHouseAdvertisement(@Path("houseCode") houseCode: String): Response<GetHouseAdvertesimentDTO>

    @POST("api/house/advertisement/create")
    suspend fun createHouseAdvertisement(@Body request: CreateHouseAdvertisementDTO): Response<String>

    @PATCH("api/house/advertisement/modify")
    suspend fun modifyHouseAdvertisement(@Body request: ModifyHouseAdvertisementDTO): Response<String>

    @DELETE("api/house/advertisement/delete/{houseCode}")
    suspend fun deleteHouseAdvertisement(@Path("houseCode") houseCode: String): Response<String>

    // --- House Management ---
    @GET("api/house/{houseCode}")
    suspend fun getHouse(@Path("houseCode") houseCode: String): Response<GetHouseDTO>

    @POST("api/house/create")
    suspend fun createHouse(@Body request: CreateHouseDTO): Response<String>

    @PATCH("api/house/modify")
    suspend fun modifyHouse(@Body request: ModifyHouseDTO): Response<String>

    @DELETE("api/house/delete/{houseCode}")
    suspend fun deleteHouse(@Path("houseCode") houseCode: String): Response<String>

    // --- Admin/User Management ---
    @POST("api/house/add/admin")
    suspend fun addAdmin(@Body request: AddAdminDTO): Response<String>

    @POST("api/house/remove/admin")
    suspend fun removeAdmin(@Body request: RemoveAdminDTO): Response<String>

    @POST("api/house/add/user")
    suspend fun addUser(@Body request: AddUserDTO): Response<String>

    @POST("api/house/remove/user")
    suspend fun removeUser(@Body request: RemoveUserDTO): Response<String>
}