package cohappy.frontend.repository

import cohappy.frontend.client.ChoreApiClient
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.HouseApiClient
import cohappy.frontend.client.NotificationApiClient
import cohappy.frontend.client.PortfolioApiClient
import cohappy.frontend.client.UserApiClient
import cohappy.frontend.client.dto.response.GetHouseDTO
import cohappy.frontend.client.dto.response.GetNextChoreDTO
import cohappy.frontend.client.dto.response.GetNotificationDTO
import cohappy.frontend.client.dto.response.UserAccountDTO
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.time.LocalDate

class HouseDashboardRepositoryTest {

    private val userApi = mockk<UserApiClient>()
    private val houseApi = mockk<HouseApiClient>()
    private val notificationApi = mockk<NotificationApiClient>()
    private val choreApi = mockk<ChoreApiClient>()
    private val portfolioApi = mockk<PortfolioApiClient>()

    private lateinit var repository: HouseDashboardRepository

    @Before
    fun setup() {
        mockkObject(ClientSingleton)
        every { ClientSingleton.userApi } returns userApi
        every { ClientSingleton.houseApi } returns houseApi
        every { ClientSingleton.notificationApi } returns notificationApi
        every { ClientSingleton.choreApi } returns choreApi
        every { ClientSingleton.portfolioApi } returns portfolioApi

        repository = HouseDashboardRepository()
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `fetchUserProfile success returns UserAccountDTO`() = runTest {
        val userCode = "USR_123"
        val expectedDto = UserAccountDTO(name = "Ale", surname = "Boss")

        coEvery { userApi.getUserProfile(userCode) } returns Response.success(expectedDto)

        val result = repository.fetchUserProfile(userCode)

        assertTrue(result.isSuccessful)
        assertEquals(expectedDto, result.body())
        coVerify(exactly = 1) { userApi.getUserProfile(userCode) }
    }

    @Test
    fun `fetchUserProfile failure returns error`() = runTest {
        val userCode = "USR_123"

        coEvery { userApi.getUserProfile(userCode) } returns Response.error<UserAccountDTO>(404, "".toResponseBody())

        val result = repository.fetchUserProfile(userCode)

        assertFalse(result.isSuccessful)
        assertEquals(404, result.code())
    }

    @Test
    fun `fetchHouseDetails success returns GetHouseDTO`() = runTest {
        val houseCode = "COH-8X2P"
        val expectedDto = GetHouseDTO(street = "Via Roma", civicNumber = 12)

        coEvery { houseApi.getHouse(houseCode) } returns Response.success(expectedDto)

        val result = repository.fetchHouseDetails(houseCode)

        assertTrue(result.isSuccessful)
        assertEquals(expectedDto, result.body())
        coVerify(exactly = 1) { houseApi.getHouse(houseCode) }
    }

    @Test
    fun `fetchNotifications success returns list of GetNotificationDTO`() = runTest {
        val userCode = "USR_123"
        val expectedList = listOf(
            GetNotificationDTO(
                eventId = "EVT_123",
                eventType = "MESSAGE",
                title = "Nuovo Messaggio",
                subtitle = "Da Anna",
                timestamp = "2024-05-12T10:00:00"
            )
        )

        coEvery { notificationApi.getUserNotifications(userCode) } returns Response.success(expectedList)

        val result = repository.fetchNotifications(userCode)

        assertTrue(result.isSuccessful)
        assertEquals(expectedList, result.body())
        coVerify(exactly = 1) { notificationApi.getUserNotifications(userCode) }
    }

    @Test
    fun `fetchNextChore success returns list of GetNextChoreDTO`() = runTest {
        val userCode = "USR_123"
        val expectedList = listOf(
            GetNextChoreDTO(
                choreCode = "CHORE_1",
                name = "Spazzare a terra",
                assignedTo = "USR_123",
                date = LocalDate.parse("2026-05-12"),
                completed = "NO"
            )
        )

        coEvery { choreApi.getNextUserChore(eq(userCode), any<LocalDate>()) } returns Response.success(expectedList)

        val result = repository.fetchNextChore(userCode)

        assertTrue(result.isSuccessful)
        assertEquals(expectedList, result.body())
        coVerify(exactly = 1) { choreApi.getNextUserChore(eq(userCode), any<LocalDate>()) }
    }

    @Test
    fun `fetchTotalDebt success returns Float`() = runTest {
        val userCode = "USR_123"
        val expectedDebt = 45.5f

        coEvery { portfolioApi.getUserTotalDebt(userCode) } returns Response.success(expectedDebt)

        val result = repository.fetchTotalDebt(userCode)

        assertTrue(result.isSuccessful)
        assertEquals(expectedDebt, result.body())
        coVerify(exactly = 1) { portfolioApi.getUserTotalDebt(userCode) }
    }
}