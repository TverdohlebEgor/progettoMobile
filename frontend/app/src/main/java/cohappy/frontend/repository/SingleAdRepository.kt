package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO
import retrofit2.Response

class SingleAdRepository {
    suspend fun fetchAdDetail(adId: String): Response<GetHouseAdvertesimentDTO> {
        return ClientSingleton.houseApi.getHouseAdvertisement(adId)
    }
}