package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.CreateHouseAdvertisementDTO
import retrofit2.Response

class CreateAdRepository {
    suspend fun createAdvertisement(dto: CreateHouseAdvertisementDTO): Response<String> {
        return ClientSingleton.houseApi.createHouseAdvertisement(dto)
    }
}