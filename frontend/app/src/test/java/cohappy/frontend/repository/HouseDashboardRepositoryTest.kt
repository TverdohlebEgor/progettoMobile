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
    fun `fetchUserProfile success`() = runTest {
        val expectedDto = UserAccountDTO(name = "Ale", houseCode = "H1")
        coEvery { userApi.getUserProfile("USR_1") } returns Response.success(expectedDto)
        val result = repository.fetchUserProfile("USR_1")
        assertTrue(result.isSuccessful)
        assertEquals(expectedDto, result.body())
    }

    @Test
    fun `fetchUserProfile error 404`() = runTest {
        coEvery { userApi.getUserProfile("USR_1") } returns Response.error(404, "".toResponseBody())
        val result = repository.fetchUserProfile("USR_1")
        assertFalse(result.isSuccessful)
        assertEquals(404, result.code())
    }

    @Test
    fun `fetchHouseDetails success`() = runTest {
        val expectedDto = GetHouseDTO(street = "Via Roma")
        coEvery { houseApi.getHouse("H1") } returns Response.success(expectedDto)
        val result = repository.fetchHouseDetails("H1")
        assertTrue(result.isSuccessful)
        assertEquals(expectedDto, result.body())
    }

    @Test
    fun `fetchHouseDetails error 404`() = runTest {
        coEvery { houseApi.getHouse("INVALID_H") } returns Response.error(404, "".toResponseBody())
        val result = repository.fetchHouseDetails("INVALID_H")
        assertFalse(result.isSuccessful)
        assertEquals(404, result.code())
    }

    @Test
    fun `fetchNotifications success`() = runTest {
        val expectedList = listOf(GetNotificationDTO("ID1", "TYPE", "Test", "Sub", "2026-05-11"))
        coEvery { notificationApi.getUserNotifications("USR_1") } returns Response.success(expectedList)
        val result = repository.fetchNotifications("USR_1")
        assertTrue(result.isSuccessful)
        assertEquals(expectedList, result.body())
    }

    // 💅 AGGIUNTO: Unhappy path per Notifications
    @Test
    fun `fetchNotifications error 500`() = runTest {
        coEvery { notificationApi.getUserNotifications("USR_1") } returns Response.error(500, "".toResponseBody())
        val result = repository.fetchNotifications("USR_1")
        assertFalse(result.isSuccessful)
        assertEquals(500, result.code())
    }

    @Test
    fun `fetchNextChore success with date`() = runTest {
        val expectedList = listOf(GetNextChoreDTO("C1", "Pulizie", "USR_1", LocalDate.now(), false))
        coEvery { choreApi.getNextUserChore(any(), any()) } returns Response.success(expectedList)
        val result = repository.fetchNextChore("USR_1")
        assertTrue(result.isSuccessful)
        assertEquals(expectedList, result.body())
    }

    @Test
    fun `fetchNextChore error 500`() = runTest {
        coEvery { choreApi.getNextUserChore(any(), any()) } returns Response.error(500, "".toResponseBody())
        val result = repository.fetchNextChore("USR_1")
        assertFalse(result.isSuccessful)
        assertEquals(500, result.code())
    }

    @Test
    fun `fetchTotalDebt success`() = runTest {
        coEvery { portfolioApi.getUserTotalDebt("USR_1") } returns Response.success(15.5f)
        val result = repository.fetchTotalDebt("USR_1")
        assertTrue(result.isSuccessful)
        assertEquals(15.5f, result.body())
    }

    @Test
    fun `fetchTotalDebt error 500`() = runTest {
        coEvery { portfolioApi.getUserTotalDebt("USR_1") } returns Response.error(500, "".toResponseBody())
        val result = repository.fetchTotalDebt("USR_1")
        assertFalse(result.isSuccessful)
        assertEquals(500, result.code())
    }
}