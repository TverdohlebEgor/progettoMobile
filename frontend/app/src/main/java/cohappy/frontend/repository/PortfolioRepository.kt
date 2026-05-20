package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.CreateDebtDTO
import cohappy.frontend.client.dto.response.GetHouseDTO
import cohappy.frontend.client.dto.response.PatchDebtPaidDTO
import cohappy.frontend.client.dto.response.PortfolioDTO
import cohappy.frontend.client.dto.response.UserAccountDTO
import cohappy.frontend.expections.ErrorMessages.SERVER_ERROR
import cohappy.frontend.expections.ErrorMessages.USER_NOT_FOUND_PORTFOLIO
import cohappy.frontend.expections.NotFoundException
import cohappy.frontend.expections.ServerErrorException

class PortfolioRepository {

    suspend fun fetchUserProfile(userCode: String): Result<UserAccountDTO> {
        return try {
            val response = ClientSingleton.userApi.getUserProfile(userCode)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.code() == 404) {
                Result.failure(NotFoundException(USER_NOT_FOUND_PORTFOLIO))
            } else {
                Result.failure(ServerErrorException(SERVER_ERROR))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchTotalBalance(userCode: String): Result<Float> {
        return try {
            val response = ClientSingleton.portfolioApi.getUserTotalDebt(userCode)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.code() == 404) {
                Result.failure(NotFoundException(USER_NOT_FOUND_PORTFOLIO))
            } else {
                Result.failure(ServerErrorException(SERVER_ERROR))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchUserPortfolio(userCode: String): Result<PortfolioDTO> {
        return try {
            val response = ClientSingleton.portfolioApi.getUserPortfolio(userCode)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.code() == 404) {
                Result.failure(NotFoundException(USER_NOT_FOUND_PORTFOLIO))
            } else {
                Result.failure(ServerErrorException(SERVER_ERROR))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchTotalDebt(userCode: String): Result<Float> {
        return try {
            val response = ClientSingleton.portfolioApi.getUserTotalDebt(userCode)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.code() == 404) {
                Result.failure(NotFoundException(USER_NOT_FOUND_PORTFOLIO))
            } else {
                Result.failure(ServerErrorException(SERVER_ERROR))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchTotalCredits(userCode: String): Result<Float> {
        return try {
            val response = ClientSingleton.portfolioApi.getUserTotalCredits(userCode)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else if (response.code() == 404) {
                Result.failure(NotFoundException(USER_NOT_FOUND_PORTFOLIO))
            } else {
                Result.failure(ServerErrorException(SERVER_ERROR))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchHouse(houseCode: String): Result<GetHouseDTO> {
        return try {
            val response = ClientSingleton.houseApi.getHouse(houseCode)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(ServerErrorException(SERVER_ERROR))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createDebt(dto: CreateDebtDTO): Result<String> {
        return try {
            val response = ClientSingleton.portfolioApi.createDebt(dto)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(ServerErrorException(SERVER_ERROR))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun patchDebtPaid(debtId: String, userCode: String, isPaid: Boolean): Result<Unit> {
        return try {
            val request = PatchDebtPaidDTO(
                debtId = debtId,
                receiverCode = userCode,
                newState = isPaid
            )
            val response = ClientSingleton.portfolioApi.patchDebtPaid(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(ServerErrorException(SERVER_ERROR))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun settleDebt(debtId: String): Result<Unit> {
        return try {
            val response = ClientSingleton.portfolioApi.deleteDebt(debtId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(ServerErrorException(SERVER_ERROR))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}