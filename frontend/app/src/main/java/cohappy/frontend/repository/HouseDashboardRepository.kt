package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.response.GetChoreDTO
import cohappy.frontend.client.dto.response.GetNotificationDTO
import cohappy.frontend.client.dto.response.UserAccountDTO
import retrofit2.Response

class HouseDashboardRepository {

    suspend fun fetchUserProfile(userCode: String): Response<UserAccountDTO> {
        return ClientSingleton.userApi.getUserProfile(userCode)
    }

    suspend fun fetchNotifications(userCode: String): Response<List<GetNotificationDTO>> {
        return ClientSingleton.notificationApi.getNotifications(userCode)
    }

    suspend fun fetchNextChore(userCode: String): Response<GetChoreDTO> {
        return ClientSingleton.choreApi.getNextChore(userCode)
    }

    suspend fun fetchTotalDebt(userCode: String): Response<Double> {
        return ClientSingleton.portfolioApi.getTotalDebt(userCode)
    }
}