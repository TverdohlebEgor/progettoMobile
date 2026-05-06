package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.response.PortfolioDTO
import cohappy.frontend.client.dto.response.UserAccountDTO
import retrofit2.Response

class PortfolioRepository {

    suspend fun fetchUserProfile(userCode: String): Response<UserAccountDTO> {
        return ClientSingleton.userApi.getUserProfile(userCode)
    }

    suspend fun fetchTotalBalance(userCode: String): Response<Float> {
        return ClientSingleton.portfolioApi.getUserTotalDebt(userCode)
    }

    suspend fun fetchUserPortfolio(userCode: String): Response<PortfolioDTO> {
        return ClientSingleton.portfolioApi.getUserPortfolio(userCode)
    }

    suspend fun fetchTotalDebt(userCode: String): Response<Float> {
        return ClientSingleton.portfolioApi.getUserTotalDebt(userCode)
    }

    suspend fun fetchTotalCredits(userCode: String): Response<Float> {
        return ClientSingleton.portfolioApi.getUserTotalCredits(userCode)
    }
}