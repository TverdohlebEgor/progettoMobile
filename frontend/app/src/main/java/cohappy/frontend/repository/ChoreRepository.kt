package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.CreateChoreDTO
import cohappy.frontend.client.dto.request.PatchChoreDTO
import cohappy.frontend.client.dto.response.GetChoreDTO
import retrofit2.Response
import java.time.LocalDate

class ChoreRepository {

    suspend fun fetchUserChores(houseCode: String): Response<List<GetChoreDTO>> {
        val today = LocalDate.now()
        return ClientSingleton.choreApi.getChore(houseCode, today)
    }

    suspend fun updateChoreStatus(patchData: PatchChoreDTO): Response<String> {
        return ClientSingleton.choreApi.patchChore(patchData)
    }

    suspend fun createChore(choreData: CreateChoreDTO): Response<String> {
        return ClientSingleton.choreApi.createChore(choreData)
    }
}