package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO
import retrofit2.Response

class AdListRepository {
    suspend fun fetchAds(): Response<List<GetHouseAdvertesimentDTO>> {
        return ClientSingleton.houseApi.getAllHouseAdvertisements()
    }
}