package cohappy.frontend.viewmodel

import cohappy.frontend.client.dto.enum.DebtType
import cohappy.frontend.client.dto.response.DebtDTO
import cohappy.frontend.client.dto.response.PortfolioDTO
import cohappy.frontend.repository.PortfolioRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
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

    private lateinit var viewModel: PortfolioViewModel
    private val repository: PortfolioRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PortfolioViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPortfolio success popola correttamente i totali e la lista movimenti`() = runTest {
        val fintoToken = "token_di_prova"
        val debitoTotale = 50.0f
        val creditoTotale = 120.0f

        val fakeDebtDto1 = DebtDTO(
            debtId = "d1",
            debtorUserCode = "token_di_prova",
            beneficiaryUserCode = "altro_user",
            amount = 10.0f,
            description = "Pizza",
            debtType = DebtType.DELIVERY_AND_EATING_OUT
        )
        val fakeDebtDto2 = DebtDTO(
            debtId = "d2",
            debtorUserCode = "altro_user",
            beneficiaryUserCode = "token_di_prova", // Sono io, è un credito
            amount = 20.0f,
            description = "Spesa",
            debtType = DebtType.GROCERIE
        )

        val fakePortfolio = PortfolioDTO(
            debts = listOf(fakeDebtDto1, fakeDebtDto2)
        )

        coEvery { repository.fetchTotalDebt(fintoToken) } returns Result.success(debitoTotale)
        coEvery { repository.fetchTotalCredits(fintoToken) } returns Result.success(creditoTotale)
        coEvery { repository.fetchUserPortfolio(fintoToken) } returns Result.success(fakePortfolio)

        viewModel.loadPortfolio(fintoToken)

        assertTrue(viewModel.isLoading)

        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertEquals(50.0, viewModel.totalDebts, 0.001)
        assertEquals(120.0, viewModel.totalCredits, 0.001)
        assertEquals(2, viewModel.transactions.size)
        assertEquals("Spesa", viewModel.transactions[0].title)
        assertFalse(viewModel.transactions[0].isDebt)
    }

    @Test
    fun `loadPortfolio fallback a zero in caso di errore di rete`() = runTest {
        val fintoToken = "token_di_prova"

        coEvery { repository.fetchTotalDebt(fintoToken) } returns Result.failure(Exception("Rete andata"))
        coEvery { repository.fetchTotalCredits(fintoToken) } returns Result.failure(Exception("Rete andata"))
        coEvery { repository.fetchUserPortfolio(fintoToken) } returns Result.failure(Exception("Rete andata"))

        viewModel.loadPortfolio(fintoToken)

        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertEquals(0.0, viewModel.totalDebts, 0.001)
        assertEquals(0.0, viewModel.totalCredits, 0.001)
        assertTrue(viewModel.transactions.isEmpty())
    }

    @Test
    fun `setFilter modifica activeFilter e getFilteredTransactions restituisce i dati giusti`() = runTest {
        val fintoToken = "token_di_prova"

        val fakeDebt = DebtDTO(
            debtId = "d1",
            debtorUserCode = "token_di_prova",
            beneficiaryUserCode = "altro",
            amount = 10.0f,
            description = "Debito",
            debtType = DebtType.DELIVERY_AND_EATING_OUT
        )
        val fakeCredit = DebtDTO(
            debtId = "d2",
            debtorUserCode = "altro",
            beneficiaryUserCode = "token_di_prova",
            amount = 20.0f,
            description = "Credito",
            debtType = DebtType.GROCERIE
        )

        coEvery { repository.fetchTotalDebt(any()) } returns Result.success(0f)
        coEvery { repository.fetchTotalCredits(any()) } returns Result.success(0f)
        coEvery { repository.fetchUserPortfolio(any()) } returns Result.success(PortfolioDTO(debts = listOf(fakeDebt, fakeCredit)))

        viewModel.loadPortfolio(fintoToken)
        advanceUntilIdle()

        assertEquals(2, viewModel.transactions.size)

        viewModel.setFilter("DEBTS")
        assertEquals("DEBTS", viewModel.activeFilter)

        val filteredDebts = viewModel.getFilteredTransactions()
        assertEquals(1, filteredDebts.size)
        assertEquals("Debito", filteredDebts[0].title)
        assertTrue(filteredDebts[0].isDebt)

        // Applichiamo filtro CREDITS
        viewModel.setFilter("CREDITS")
        assertEquals("CREDITS", viewModel.activeFilter)

        val filteredCredits = viewModel.getFilteredTransactions()
        assertEquals(1, filteredCredits.size)
        assertEquals("Credito", filteredCredits[0].title)
        assertFalse(filteredCredits[0].isDebt)
    }
}