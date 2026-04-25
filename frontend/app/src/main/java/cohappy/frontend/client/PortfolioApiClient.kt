package cohappy.frontend.client

import cohappy.frontend.client.dto.request.CreateDebtDTO
import cohappy.frontend.client.dto.request.MoveMoneyDTO
import cohappy.frontend.client.dto.request.SendMoneyDTO
import cohappy.frontend.client.dto.response.PortfolioDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface PortfolioApiClient {

    @GET("api/portafolio/{userCode}")
    suspend fun getUserPortfolio(@Path("userCode") userCode: String): Response<PortfolioDTO>

    @PATCH("api/portafolio/money/move")
    suspend fun moveMoney(@Body request: MoveMoneyDTO): Response<String>

    @PATCH("api/portafolio/money/send")
    suspend fun sendMoney(@Body request: SendMoneyDTO): Response<String>

    @POST("api/portafolio/debt/create")
    suspend fun createDebt(@Body request: CreateDebtDTO): Response<String>

    @DELETE("api/portafolio/debt/delete/{debtId}")
    suspend fun deleteDebt(@Path("debtId") debtId: String): Response<String>
}