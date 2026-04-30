package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.PatchChoreDTO
import retrofit2.Response

class ChoresRepository {

    suspend fun updateChoreStatus(patchData: PatchChoreDTO): Response<String> {
        return ClientSingleton.choreApi.patchChore(patchData)
    }

}