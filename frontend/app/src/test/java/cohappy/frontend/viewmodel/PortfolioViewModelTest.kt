package cohappy.frontend.viewmodel

import cohappy.frontend.client.dto.response.PortfolioDTO
import cohappy.frontend.repository.PortfolioRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioViewModelTest {

    private val repository = mockk<PortfolioRepository>()
    private lateinit var viewModel: PortfolioViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PortfolioViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPortfolio success updates state`() = runTest(testDispatcher) {
        val userToken = "test_token"
        val portfolio = PortfolioDTO(amount = 1000.0f, debts = emptyList())
        
        coEvery { repository.fetchTotalDebt(any()) } returns Result.success(100.0f)
        coEvery { repository.fetchTotalCredits(any()) } returns Result.success(200.0f)
        coEvery { repository.fetchUserPortfolio(any()) } returns Result.success(portfolio)

        viewModel.loadPortfolio(userToken)
        
        assertTrue(viewModel.isLoading)
        runCurrent()
        
        assertFalse(viewModel.isLoading)
        assertEquals(100.0, viewModel.totalDebts, 0.01)
        assertEquals(200.0, viewModel.totalCredits, 0.01)
    }

    @Test
    fun `loadPortfolio failure sets defaults`() = runTest(testDispatcher) {
        val userToken = "test_token"
        
        coEvery { repository.fetchTotalDebt(any()) } returns Result.failure(Exception())
        coEvery { repository.fetchTotalCredits(any()) } returns Result.failure(Exception())
        coEvery { repository.fetchUserPortfolio(any()) } returns Result.failure(Exception())

        viewModel.loadPortfolio(userToken)
        runCurrent()
        
        assertFalse(viewModel.isLoading)
        assertEquals(0.0, viewModel.totalDebts, 0.01)
        assertEquals(0.0, viewModel.totalCredits, 0.01)
        assertTrue(viewModel.transactions.isEmpty())
    }

    @Test
    fun `setFilter updates activeFilter`() {
        viewModel.setFilter("DEBTS")
        assertEquals("DEBTS", viewModel.activeFilter)
    }
}
