package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO
import cohappy.frontend.expections.ErrorMessages.SERVER_ERROR
import cohappy.frontend.expections.ServerErrorException
import retrofit2.Response

class AdListRepository {
    suspend fun fetchAds(): Result<List<GetHouseAdvertesimentDTO>> {
        return try {
            val response = ClientSingleton.houseApi.getAllHouseAdvertisements()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(ServerErrorException(SERVER_ERROR))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}