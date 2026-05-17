package cohappy.frontend.viewmodel

import android.util.Log
import cohappy.frontend.client.dto.response.GetHouseDTO
import cohappy.frontend.client.dto.response.GetNextChoreDTO
import cohappy.frontend.client.dto.response.GetNotificationDTO
import cohappy.frontend.client.dto.response.UserAccountDTO
import cohappy.frontend.repository.HouseDashboardRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class HouseDashboardViewModelTest {

    private lateinit var viewModel: HouseDashboardViewModel
    private val repository: HouseDashboardRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0

        viewModel = HouseDashboardViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
        unmockkAll()
    }

    @Test
    fun `loadDashboardData happy path updates all state variables`() = runTest {
        val userToken = "token_123"
        val houseCode = "CASA_123"

        val mockUser = UserAccountDTO(name = "Ale", houseCode = houseCode)
        val mockHouse = GetHouseDTO(street = "Via Roma", civicNumber = 10)
        val mockNotifications = listOf(GetNotificationDTO(eventId = "E1", eventType = "T1", title = "Notifica 1", subtitle = "Sub", timestamp = "2023-10-27T10:00:00"))
        val mockChore = GetNextChoreDTO(choreCode = "C1", name = "Lavare terra", assignedTo = "User1", date = LocalDate.now(), completed = false)
        val mockDebt = 15.50f

        coEvery { repository.fetchUserProfile(any()) } returns Response.success(mockUser)
        coEvery { repository.fetchHouseDetails(houseCode) } returns Response.success(mockHouse)
        coEvery { repository.fetchNotifications(any()) } returns Response.success(mockNotifications)
        coEvery { repository.fetchNextChore(any()) } returns Response.success(listOf(mockChore))
        coEvery { repository.fetchTotalDebt(any()) } returns Response.success(mockDebt)

        viewModel.loadDashboardData(userToken, houseCode)
        assertTrue(viewModel.isLoading)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertEquals("Ale", viewModel.nomeUtente)
        assertEquals("Via Roma 10", viewModel.houseAddress)
        assertEquals(1, viewModel.notifications.size)
        assertEquals("Lavare terra", viewModel.nextChoreName)
        assertEquals("Oggi", viewModel.nextChoreDeadline)
        // Locale.getDefault() could be anything, but we expect 15.50 with some separator
        assertTrue(viewModel.totalDebtAmount.contains("15") && viewModel.totalDebtAmount.contains("50"))
    }

    @Test
    fun `loadDashboardData unhappy path sets fallback values`() = runTest {
        val userToken = "token_123"
        val houseCode = "CASA_123"

        coEvery { repository.fetchUserProfile(any()) } throws Exception("Network Error")
        coEvery { repository.fetchHouseDetails(any()) } throws Exception("Network Error")
        coEvery { repository.fetchNotifications(any()) } throws Exception("Network Error")
        coEvery { repository.fetchNextChore(any()) } throws Exception("Network Error")
        coEvery { repository.fetchTotalDebt(any()) } throws Exception("Network Error")

        viewModel.loadDashboardData(userToken, houseCode)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertEquals("Offline", viewModel.nomeUtente)
        assertEquals("Offline", viewModel.houseAddress)
        assertTrue(viewModel.notifications.isEmpty())
        assertEquals("Nessuna", viewModel.nextChoreName)
        assertEquals("Tocca a te", viewModel.nextChoreDeadline)
        assertEquals("0,00 €", viewModel.totalDebtAmount)
    }

    @Test
    fun `loadDashboardData with 204 next chore sets default text`() = runTest {
        val userToken = "token_123"
        val houseCode = "CASA_123"
        val mockUser = UserAccountDTO(name = "Ale")

        coEvery { repository.fetchUserProfile(any()) } returns Response.success(mockUser)
        coEvery { repository.fetchHouseDetails(any()) } returns Response.success(GetHouseDTO())
        coEvery { repository.fetchNotifications(any()) } returns Response.success(emptyList())
        coEvery { repository.fetchNextChore(any()) } returns Response.error(204, "".toResponseBody())
        coEvery { repository.fetchTotalDebt(any()) } returns Response.success(0.0f)

        viewModel.loadDashboardData(userToken, houseCode)
        advanceUntilIdle()

        assertEquals("Nessuna", viewModel.nextChoreName)
        assertEquals("Tocca a te", viewModel.nextChoreDeadline)
    }
}