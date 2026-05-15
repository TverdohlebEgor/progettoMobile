package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.PortfolioApiClient
import cohappy.frontend.client.dto.response.PortfolioDTO
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class PortfolioRepositoryTest {

    private val portfolioApi = mockk<PortfolioApiClient>()
    private lateinit var repository: PortfolioRepository

    @Before
    fun setup() {
        mockkObject(ClientSingleton)
        every { ClientSingleton.portfolioApi } returns portfolioApi
        repository = PortfolioRepository()
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun testFetchUserPortfolioSuccess() = runTest {
        val userCode = "USR_123"
        val expectedPortfolio = PortfolioDTO(debts = emptyList())
        coEvery { portfolioApi.getUserPortfolio(userCode) } returns Response.success(expectedPortfolio)

        val result = repository.fetchUserPortfolio(userCode)

        assertTrue(result.isSuccess)
        assertEquals(expectedPortfolio, result.getOrNull())
    }

    @Test
    fun testFetchUserPortfolioError404() = runTest {
        val userCode = "USR_123"
        coEvery { portfolioApi.getUserPortfolio(userCode) } returns Response.error(404, "".toResponseBody())

        val result = repository.fetchUserPortfolio(userCode)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotFoundException)
    }

    @Test
    fun testFetchTotalDebtSuccess() = runTest {
        val userCode = "USR_123"
        val expectedDebt = 150.5f
        coEvery { portfolioApi.getUserTotalDebt(userCode) } returns Response.success(expectedDebt)

        val result = repository.fetchTotalDebt(userCode)

        assertTrue(result.isSuccess)
        assertEquals(expectedDebt, result.getOrNull())
    }

    @Test
    fun testFetchTotalDebtError500() = runTest {
        val userCode = "USR_123"
        coEvery { portfolioApi.getUserTotalDebt(userCode) } returns Response.error(500, "".toResponseBody())

        val result = repository.fetchTotalDebt(userCode)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ServerErrorException)
    }

    @Test
    fun testFetchTotalCreditsSuccess() = runTest {
        val userCode = "USR_123"
        val expectedCredits = 75.0f
        coEvery { portfolioApi.getUserTotalCredits(userCode) } returns Response.success(expectedCredits)

        val result = repository.fetchTotalCredits(userCode)

        assertTrue(result.isSuccess)
        assertEquals(expectedCredits, result.getOrNull())
    }

    @Test
    fun testFetchTotalCreditsError400() = runTest {
        val userCode = "USR_123"
        coEvery { portfolioApi.getUserTotalCredits(userCode) } returns Response.error(400, "".toResponseBody())

        val result = repository.fetchTotalCredits(userCode)

        assertTrue(result.isFailure)
        // Note: Repository maps any non-404 error to ServerErrorException
        assertTrue(result.exceptionOrNull() is ServerErrorException)
    }
}