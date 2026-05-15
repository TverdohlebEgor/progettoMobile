package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO
import cohappy.frontend.expections.ErrorMessages.SERVER_ERROR
import cohappy.frontend.expections.ServerErrorException

class AdListRepository {
    suspend fun fetchAds(): Result<List<GetHouseAdvertesimentDTO>> {
        try {
            val apiResponse = ClientSingleton.houseApi.getAllHouseAdvertisements()
            if (apiResponse.isSuccessful && apiResponse.body() != null) {
                return Result.success(apiResponse.body()!!)
            } else {
                return Result.failure(ServerErrorException(SERVER_ERROR))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}