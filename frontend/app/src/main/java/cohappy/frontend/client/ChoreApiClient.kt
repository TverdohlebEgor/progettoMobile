package cohappy.frontend.client

import cohappy.frontend.client.dto.request.CreateChoreDTO
import cohappy.frontend.client.dto.request.PatchChoreDTO
import cohappy.frontend.client.dto.response.GetChoreDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import java.time.LocalDate

interface ChoreApiClient {

    @GET("api/chore/{houseCode}/{date}")
    suspend fun getChore(
        @Path("houseCode") houseCode: String,
        @Path("date") date: LocalDate
    ): Response<List<GetChoreDTO>>

    @PATCH("api/chore/patch")
    suspend fun patchChore(@Body request: PatchChoreDTO): Response<String>

    @POST("api/chore/create")
    suspend fun createChore(@Body request: CreateChoreDTO): Response<String>
}