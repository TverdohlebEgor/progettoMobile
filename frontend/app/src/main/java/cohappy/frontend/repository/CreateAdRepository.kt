package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.CreateHouseAdvertisementDTO
import cohappy.frontend.expections.ErrorMessages.CREATE_HOME_BAD_REQUEST
import cohappy.frontend.expections.ErrorMessages.SERVER_ERROR
import cohappy.frontend.expections.ErrorMessages.USER_NOT_FOUND_CREATE_HOUSE
import cohappy.frontend.expections.ServerErrorException
import retrofit2.Response

class CreateAdRepository {
    suspend fun createAdvertisement(dto: CreateHouseAdvertisementDTO): Result<String> {
        return try {
            val apiResponse = ClientSingleton.houseApi.createHouseAdvertisement(dto)
            if (apiResponse.isSuccessful && apiResponse.body() != null) {
                Result.success(apiResponse.body()!!)
            } else {
                when(apiResponse.code()){
                    400 -> Result.failure(ServerErrorException(CREATE_HOME_BAD_REQUEST))
                    404 -> Result.failure(ServerErrorException(USER_NOT_FOUND_CREATE_HOUSE))
                    else -> Result.failure(ServerErrorException(SERVER_ERROR))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}