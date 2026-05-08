package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.PortfolioApiClient
import cohappy.frontend.client.UserApiClient
import cohappy.frontend.client.dto.response.PortfolioDTO
import cohappy.frontend.client.dto.response.UserAccountDTO
import cohappy.frontend.expections.ErrorMessages.SERVER_ERROR
import cohappy.frontend.expections.ErrorMessages.USER_NOT_FOUND_PORTFOLIO
import cohappy.frontend.expections.NotFoundException
import cohappy.frontend.expections.ServerErrorException
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class PortfolioRepositoryTest {

    private val userApi = mockk<UserApiClient>()
    private val portfolioApi = mockk<PortfolioApiClient>()
    private lateinit var repository: PortfolioRepository

    @Before
    fun setup() {
        mockkObject(ClientSingleton)
        every { ClientSingleton.userApi } returns userApi
        every { ClientSingleton.portfolioApi } returns portfolioApi
        repository = PortfolioRepository()
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `fetchUserProfile success`() = runTest {
        val userCode = "USR_123"
        val expectedProfile = UserAccountDTO("USR_123", "Test User", "test@example.com", "path/to/img")
        coEvery { userApi.getUserProfile(userCode) } returns Response.success(expectedProfile)

        val result = repository.fetchUserProfile(userCode)

        assertTrue(result.isSuccess)
        assertEquals(expectedProfile, result.getOrNull())
    }

    @Test
    fun `fetchUserProfile failure 404`() = runTest {
        val userCode = "NON_EXISTENT"
        coEvery { userApi.getUserProfile(userCode) } returns Response.error(404, "".toResponseBody())

        val result = repository.fetchUserProfile(userCode)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotFoundException)
        assertEquals(USER_NOT_FOUND_PORTFOLIO, result.exceptionOrNull()?.message)
    }

    @Test
    fun `fetchTotalDebt success`() = runTest {
        val userCode = "USR_123"
        val expectedDebt = 150.0f
        coEvery { portfolioApi.getUserTotalDebt(userCode) } returns Response.success(expectedDebt)

        val result = repository.fetchTotalDebt(userCode)

        assertTrue(result.isSuccess)
        assertEquals(expectedDebt, result.getOrNull())
    }

    @Test
    fun `fetchTotalCredits success`() = runTest {
        val userCode = "USR_123"
        val expectedCredits = 200.0f
        coEvery { portfolioApi.getUserTotalCredits(userCode) } returns Response.success(expectedCredits)

        val result = repository.fetchTotalCredits(userCode)

        assertTrue(result.isSuccess)
        assertEquals(expectedCredits, result.getOrNull())
    }

    @Test
    fun `fetchUserPortfolio success`() = runTest {
        val userCode = "USR_123"
        val expectedPortfolio = PortfolioDTO(100.0f, emptyList())
        coEvery { portfolioApi.getUserPortfolio(userCode) } returns Response.success(expectedPortfolio)

        val result = repository.fetchUserPortfolio(userCode)

        assertTrue(result.isSuccess)
        assertEquals(expectedPortfolio, result.getOrNull())
    }

    @Test
    fun `fetchUserProfile generic error 500`() = runTest {
        val userCode = "USR_123"
        coEvery { userApi.getUserProfile(userCode) } returns Response.error(500, "".toResponseBody())

        val result = repository.fetchUserProfile(userCode)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ServerErrorException)
        assertEquals(SERVER_ERROR, result.exceptionOrNull()?.message)
    }

    @Test
    fun `fetchUserProfile exception`() = runTest {
        val userCode = "USR_123"
        val exception = Exception("Network error")
        coEvery { userApi.getUserProfile(userCode) } throws exception

        val result = repository.fetchUserProfile(userCode)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
