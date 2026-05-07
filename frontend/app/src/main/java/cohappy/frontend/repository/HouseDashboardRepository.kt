package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.response.GetHouseDTO
import cohappy.frontend.client.dto.response.GetNextChoreDTO
import cohappy.frontend.client.dto.response.GetNotificationDTO
import cohappy.frontend.client.dto.response.UserAccountDTO
import retrofit2.Response
import java.time.LocalDate

class HouseDashboardRepository {

    suspend fun fetchUserProfile(userCode: String): Response<UserAccountDTO> {
        return ClientSingleton.userApi.getUserProfile(userCode)
    }

    suspend fun fetchNotifications(userCode: String): Response<List<GetNotificationDTO>> {
        return ClientSingleton.notificationApi.getUserNotifications(userCode)
    }
    suspend fun fetchTotalDebt(userCode: String): Response<Float> {
        return ClientSingleton.portfolioApi.getUserTotalDebt(userCode)
    }

    suspend fun fetchHouseDetails(houseCode: String): Response<GetHouseDTO> {
        return ClientSingleton.houseApi.getHouse(houseCode)
    }

    suspend fun fetchNextChore(userCode: String): Response<List<GetNextChoreDTO>> {
        val oggi = LocalDate.now()
        return ClientSingleton.choreApi.getNextUserChore(userCode, oggi)
    }
}