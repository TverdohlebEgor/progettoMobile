package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.PortfolioApiClient
import cohappy.frontend.client.UserApiClient
import cohappy.frontend.client.dto.response.PortfolioDTO
import cohappy.frontend.client.dto.response.UserAccountDTO
import cohappy.frontend.expections.NotFoundException
import cohappy.frontend.expections.ServerErrorException
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioRepositoryTest {

    private lateinit var repository: PortfolioRepository
    private val mockPortfolioApi = mockk<PortfolioApiClient>()
    private val mockUserApi = mockk<UserApiClient>()

    @Before
    fun setUp() {
        mockkObject(ClientSingleton)
        ClientSingleton.portfolioApi = mockPortfolioApi
        ClientSingleton.userApi = mockUserApi
        repository = PortfolioRepository()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // --- HAPPY PATHS ---

    @Test
    fun `fetchUserPortfolio - Happy Path - Ritorna PortfolioDTO`() = runTest {
        val mockPortfolio = PortfolioDTO(debts = emptyList())
        coEvery { mockPortfolioApi.getUserPortfolio("TOKEN_BADDIE") } returns Response.success(mockPortfolio)

        val result = repository.fetchUserPortfolio("TOKEN_BADDIE")

        assertTrue(result.isSuccess)
        assertEquals(mockPortfolio, result.getOrNull())
    }

    @Test
    fun `fetchTotalDebt - Happy Path - Ritorna Float`() = runTest {
        coEvery { mockPortfolioApi.getUserTotalDebt("TOKEN_BADDIE") } returns Response.success(150.5f)

        val result = repository.fetchTotalDebt("TOKEN_BADDIE")

        assertTrue(result.isSuccess)
        assertEquals(150.5f, result.getOrNull())
    }

    @Test
    fun `settleDebt - Happy Path - Ritorna Unit`() = runTest {
        coEvery { mockPortfolioApi.deleteDebt("DEBT_123") } returns Response.success("OK")

        val result = repository.settleDebt("DEBT_123")

        assertTrue(result.isSuccess)
    }

    // --- BAD PATHS ---

    @Test
    fun `fetchUserPortfolio - Bad Path - Ritorna NotFoundException su 404`() = runTest {
        coEvery { mockPortfolioApi.getUserPortfolio("TOKEN_GHOST") } returns Response.error(404, "".toResponseBody())

        val result = repository.fetchUserPortfolio("TOKEN_GHOST")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotFoundException)
    }

    @Test
    fun `fetchTotalCredits - Bad Path - Ritorna ServerErrorException su 500`() = runTest {
        coEvery { mockPortfolioApi.getUserTotalCredits("TOKEN_ERROR") } returns Response.error(500, "".toResponseBody())

        val result = repository.fetchTotalCredits("TOKEN_ERROR")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ServerErrorException)
    }
}