package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.CreateHouseAdvertisementDTO
import retrofit2.Response

class CreateAdRepository {
    suspend fun createAdvertisement(dto: cohappy.frontend.client.dto.request.CreateHouseAdvertisementDTO): Response<String> {
        return ClientSingleton.houseApi.createHouseAdvertisement(dto)
    }

    suspend fun getAdvertisement(houseCode: String): Response<cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO> {
        return ClientSingleton.houseApi.getHouseAdvertisement(houseCode)
    }

    suspend fun modifyAdvertisement(dto: cohappy.frontend.client.dto.request.ModifyHouseAdvertisementDTO): Response<String> {
        return ClientSingleton.houseApi.modifyHouseAdvertisement(dto)
    }
}