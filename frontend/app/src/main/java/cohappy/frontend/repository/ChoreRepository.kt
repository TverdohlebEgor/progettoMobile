package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.CreateChoreDTO
import cohappy.frontend.client.dto.request.PatchChoreDTO
import cohappy.frontend.client.dto.response.GetChoreDTO
import cohappy.frontend.client.dto.response.GetHouseDTO
import cohappy.frontend.client.dto.response.UserAccountDTO
import retrofit2.Response
import java.time.LocalDate

class ChoreRepository {

    suspend fun getUserProfile(userCode: String): Response<UserAccountDTO> {
        return ClientSingleton.userApi.getUserProfile(userCode)
    }

    suspend fun getHouseDetails(houseCode: String): Response<GetHouseDTO> {
        return ClientSingleton.houseApi.getHouse(houseCode)
    }

    suspend fun fetchChores(houseCode: String, date: LocalDate): Response<List<GetChoreDTO>> {
        return ClientSingleton.choreApi.getChore(houseCode, date)
    }

    suspend fun updateChoreStatus(patchData: PatchChoreDTO): Response<String> {
        return ClientSingleton.choreApi.patchChore(patchData)
    }

    suspend fun createChore(choreData: CreateChoreDTO): Response<String> {
        return ClientSingleton.choreApi.createChore(choreData)
    }
}
