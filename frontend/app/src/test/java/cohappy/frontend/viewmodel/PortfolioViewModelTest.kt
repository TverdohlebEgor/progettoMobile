package cohappy.frontend.viewmodel

import cohappy.frontend.client.dto.response.DebtDTO
import cohappy.frontend.client.dto.response.PortfolioDTO
import cohappy.frontend.client.dto.response.UserAccountDTO
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.enum.DebtType
import cohappy.frontend.repository.PortfolioRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
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

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioViewModelTest {

    private lateinit var viewModel: PortfolioViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(ClientSingleton)
        viewModel = PortfolioViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
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
            beneficiaryUserCode = "token_di_prova",
            amount = 20.0f,
            description = "Spesa",
            debtType = DebtType.GROCERIE
        )

        val fakePortfolio = PortfolioDTO(
            debts = listOf(fakeDebtDto1, fakeDebtDto2)
        )

        val portfolioRepo = mockk<PortfolioRepository>()
        coEvery { portfolioRepo.fetchTotalDebt(fintoToken) } returns Result.success(debitoTotale)
        coEvery { portfolioRepo.fetchTotalCredits(fintoToken) } returns Result.success(creditoTotale)
        coEvery { portfolioRepo.fetchUserPortfolio(fintoToken) } returns Result.success(fakePortfolio)

        coEvery { ClientSingleton.userApi.getUserProfile(fintoToken) } returns Response.success(UserAccountDTO(houseCode = null))

        val viewModelWithMock = PortfolioViewModel(portfolioRepo)
        viewModelWithMock.loadPortfolio(fintoToken)

        assertTrue(viewModelWithMock.isLoading)

        advanceUntilIdle()

        assertFalse(viewModelWithMock.isLoading)
        assertEquals(50.0, viewModelWithMock.totalDebts, 0.001)
        assertEquals(120.0, viewModelWithMock.totalCredits, 0.001)
        assertEquals(2, viewModelWithMock.transactions.size)
        assertEquals("Spesa", viewModelWithMock.transactions[0].title)
        assertFalse(viewModelWithMock.transactions[0].isDebt)
    }

    @Test
    fun `loadPortfolio fallback a zero in caso di errore di rete`() = runTest {
        val fintoToken = "token_di_prova"
        val portfolioRepo = mockk<PortfolioRepository>()

        coEvery { portfolioRepo.fetchTotalDebt(fintoToken) } returns Result.failure(Exception("Error"))
        coEvery { portfolioRepo.fetchTotalCredits(fintoToken) } returns Result.failure(Exception("Error"))
        coEvery { portfolioRepo.fetchUserPortfolio(fintoToken) } returns Result.failure(Exception("Error"))
        coEvery { ClientSingleton.userApi.getUserProfile(fintoToken) } returns Response.error(500, "".toResponseBody())

        val viewModelWithMock = PortfolioViewModel(portfolioRepo)
        viewModelWithMock.loadPortfolio(fintoToken)

        advanceUntilIdle()

        assertFalse(viewModelWithMock.isLoading)
        assertEquals(0.0, viewModelWithMock.totalDebts, 0.001)
        assertEquals(0.0, viewModelWithMock.totalCredits, 0.001)
        assertTrue(viewModelWithMock.transactions.isEmpty())
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

        val portfolioRepo = mockk<PortfolioRepository>()
        coEvery { portfolioRepo.fetchTotalDebt(any()) } returns Result.success(0f)
        coEvery { portfolioRepo.fetchTotalCredits(any()) } returns Result.success(0f)
        coEvery { portfolioRepo.fetchUserPortfolio(any()) } returns Result.success(PortfolioDTO(debts = listOf(fakeDebt, fakeCredit)))
        coEvery { ClientSingleton.userApi.getUserProfile(fintoToken) } returns Response.success(UserAccountDTO(houseCode = null))

        val viewModelWithMock = PortfolioViewModel(portfolioRepo)
        viewModelWithMock.loadPortfolio(fintoToken)
        advanceUntilIdle()

        assertEquals(2, viewModelWithMock.transactions.size)

        viewModelWithMock.setFilter("DEBTS")
        assertEquals("DEBTS", viewModelWithMock.activeFilter)

        val filteredDebts = viewModelWithMock.getFilteredTransactions()
        assertEquals(1, filteredDebts.size)
        assertEquals("Debito", filteredDebts[0].title)
        assertTrue(filteredDebts[0].isDebt)

        viewModelWithMock.setFilter("CREDITS")
        assertEquals("CREDITS", viewModelWithMock.activeFilter)

        val filteredCredits = viewModelWithMock.getFilteredTransactions()
        assertEquals(1, filteredCredits.size)
        assertEquals("Credito", filteredCredits[0].title)
        assertFalse(filteredCredits[0].isDebt)
    }

    @Test
    fun `createDebt correctly updates state`() = runTest {
        val fintoToken = "token_di_prova"
        val portfolioRepo = mockk<PortfolioRepository>()
        val viewModelWithMock = PortfolioViewModel(portfolioRepo)

        viewModelWithMock.updateNewDebtAmount("100.0")
        viewModelWithMock.updateNewDebtTitle("Spesa")
        viewModelWithMock.updateNewDebtCategory(DebtType.GROCERIE)
        viewModelWithMock.updateNewDebtReceiver("user_2")

        coEvery { ClientSingleton.portfolioApi.createDebt(any()) } returns Response.success("NEW_DEBT_ID")

        coEvery { portfolioRepo.fetchTotalDebt(fintoToken) } returns Result.success(0f)
        coEvery { portfolioRepo.fetchTotalCredits(fintoToken) } returns Result.success(0f)
        coEvery { portfolioRepo.fetchUserPortfolio(fintoToken) } returns Result.success(PortfolioDTO(debts = emptyList()))
        coEvery { ClientSingleton.userApi.getUserProfile(fintoToken) } returns Response.success(UserAccountDTO(houseCode = null))

        viewModelWithMock.createDebt(fintoToken)
        advanceUntilIdle()

        assertFalse(viewModelWithMock.isAddingDebt)
        assertFalse(viewModelWithMock.showAddDebtSheet)
    }

    @Test
    fun `createDebt does nothing if amount is invalid or receiver is blank`() = runTest {
        val fintoToken = "token_di_prova"
        val portfolioRepo = mockk<PortfolioRepository>()
        val viewModelWithMock = PortfolioViewModel(portfolioRepo)

        viewModelWithMock.openAddDebtSheet()

        viewModelWithMock.updateNewDebtAmount("")
        viewModelWithMock.updateNewDebtTitle("Spesa")
        viewModelWithMock.createDebt(fintoToken)
        advanceUntilIdle()
        assertTrue(viewModelWithMock.showAddDebtSheet)

        viewModelWithMock.updateNewDebtAmount("100.0")
        viewModelWithMock.updateNewDebtReceiver("")
        viewModelWithMock.createDebt(fintoToken)
        advanceUntilIdle()
        assertTrue(viewModelWithMock.showAddDebtSheet)
    }
}