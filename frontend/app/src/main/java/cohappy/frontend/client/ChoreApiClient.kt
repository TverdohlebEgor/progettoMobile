package cohappy.frontend.client

import cohappy.frontend.model.dto.response.GetChoreDTO
import cohappy.frontend.model.dto.request.PatchChoreDTO
import cohappy.frontend.model.dto.request.CreateChoreDTO
import retrofit2.Response
import retrofit2.http.*
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