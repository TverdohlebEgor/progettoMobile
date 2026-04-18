package cohappy.frontend.client

import cohappy.frontend.model.dto.response.PortfolioDTO
import cohappy.frontend.model.dto.request.CreateDebtDTO
import cohappy.frontend.model.dto.request.MoveMoneyDTO
import cohappy.frontend.model.dto.request.SendMoneyDTO
import retrofit2.Response
import retrofit2.http.*

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