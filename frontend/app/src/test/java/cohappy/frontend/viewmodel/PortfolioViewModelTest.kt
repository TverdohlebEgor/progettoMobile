package cohappy.frontend.viewmodel

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.enum.DebtType
import cohappy.frontend.client.dto.response.DebtDTO
import cohappy.frontend.client.dto.response.PortfolioDTO
import cohappy.frontend.client.dto.response.UserAccountDTO
import cohappy.frontend.repository.PortfolioRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioViewModelTest {

    private lateinit var viewModel: PortfolioViewModel
    private val portfolioRepo = mockk<PortfolioRepository>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Mock static Dispatchers to redirect IO to test dispatcher
        mockkStatic(Dispatchers::class)
        every { Dispatchers.IO } returns testDispatcher
        
        mockkObject(ClientSingleton)
        // Mock the APIs inside ClientSingleton
        every { ClientSingleton.userApi } returns mockk(relaxed = true)
        every { ClientSingleton.portfolioApi } returns mockk(relaxed = true)
        every { ClientSingleton.houseApi } returns mockk(relaxed = true)
        
        viewModel = PortfolioViewModel(portfolioRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    private fun setupSuccessfulLoad(token: String) {
        coEvery { portfolioRepo.fetchTotalDebt(token) } returns Result.success(50.0f)
        coEvery { portfolioRepo.fetchTotalCredits(token) } returns Result.success(100.0f)
        coEvery { portfolioRepo.fetchUserProfile(token) } returns Result.success(UserAccountDTO(userCode = token))
        coEvery { portfolioRepo.fetchUserPortfolio(token) } returns Result.success(PortfolioDTO(debts = emptyList()))
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

        coEvery { portfolioRepo.fetchTotalDebt(fintoToken) } returns Result.success(debitoTotale)
        coEvery { portfolioRepo.fetchTotalCredits(fintoToken) } returns Result.success(creditoTotale)
        coEvery { portfolioRepo.fetchUserPortfolio(fintoToken) } returns Result.success(fakePortfolio)
        coEvery { portfolioRepo.fetchUserProfile(fintoToken) } returns Result.success(UserAccountDTO(userCode = fintoToken))

        println("DEBUG: repo mock = $portfolioRepo")

        viewModel.loadPortfolio(fintoToken)

        assertTrue(viewModel.isLoading)

        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertEquals(50.0, viewModel.totalDebts, 0.001)
        assertEquals(120.0, viewModel.totalCredits, 0.001)
        assertEquals(2, viewModel.transactions.size)
        
        // Il ViewModel fa .reversed(), quindi il secondo debito aggiunto in lista è il primo in transactions
        assertEquals("Spesa", viewModel.transactions[0].title)
        assertFalse(viewModel.transactions[0].isDebt) // "altro_user" deve a me -> credito
    }

    @Test
    fun `loadPortfolio fallback a zero in caso di errore di rete`() = runTest {
        val fintoToken = "token_di_prova"

        coEvery { portfolioRepo.fetchTotalDebt(any()) } returns Result.failure(Exception("Error"))
        coEvery { portfolioRepo.fetchTotalCredits(any()) } returns Result.failure(Exception("Error"))
        coEvery { portfolioRepo.fetchUserPortfolio(any()) } returns Result.failure(Exception("Error"))
        coEvery { portfolioRepo.fetchUserProfile(any()) } returns Result.failure(Exception("Error"))

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

        coEvery { portfolioRepo.fetchTotalDebt(any()) } returns Result.success(0f)
        coEvery { portfolioRepo.fetchTotalCredits(any()) } returns Result.success(0f)
        coEvery { portfolioRepo.fetchUserPortfolio(any()) } returns Result.success(PortfolioDTO(debts = listOf(fakeDebt, fakeCredit)))
        coEvery { portfolioRepo.fetchUserProfile(any()) } returns Result.success(UserAccountDTO(userCode = fintoToken))

        viewModel.loadPortfolio(fintoToken)
        advanceUntilIdle()

        assertEquals(2, viewModel.transactions.size)

        viewModel.setFilter("DEBTS")
        assertEquals("DEBTS", viewModel.activeFilter)

        val filteredDebts = viewModel.getFilteredTransactions()
        assertEquals(1, filteredDebts.size)
        assertEquals("Debito", filteredDebts[0].title)
        assertTrue(filteredDebts[0].isDebt)

        viewModel.setFilter("CREDITS")
        assertEquals("CREDITS", viewModel.activeFilter)

        val filteredCredits = viewModel.getFilteredTransactions()
        assertEquals(1, filteredCredits.size)
        assertEquals("Credito", filteredCredits[0].title)
        assertFalse(filteredCredits[0].isDebt)
    }

    @Test
    fun `createDebt correctly calls API and reloads portfolio`() = runTest {
        val fintoToken = "token_di_prova"

        // Setup per inizializzare currentUserCode
        setupSuccessfulLoad(fintoToken)
        viewModel.loadPortfolio(fintoToken)
        advanceUntilIdle()

        viewModel.openAddDebtSheet()
        viewModel.updateNewDebtAmount("100.0")
        viewModel.updateNewDebtTitle("Spesa")
        viewModel.updateNewDebtCategory(DebtType.GROCERIE)
        viewModel.toggleRoommateSelection("user_2")

        coEvery { ClientSingleton.portfolioApi.createDebt(any()) } returns Response.success("OK")

        viewModel.createDebt(fintoToken)
        advanceUntilIdle()

        assertFalse(viewModel.isAddingDebt)
        assertFalse(viewModel.showAddDebtSheet)
        coVerify(atLeast = 1) { ClientSingleton.portfolioApi.createDebt(any()) }
    }

    @Test
    fun `debug createDebt execution`() = runTest {
        val fintoToken = "token_di_prova"
        setupSuccessfulLoad(fintoToken)
        viewModel.loadPortfolio(fintoToken)
        advanceUntilIdle()

        viewModel.openAddDebtSheet()
        viewModel.updateNewDebtAmount("100.0")
        viewModel.updateNewDebtTitle("Spesa")
        viewModel.toggleRoommateSelection("user_2")

        println("DEBUG: isAddingDebt before = ${viewModel.isAddingDebt}")
        println("DEBUG: showAddDebtSheet before = ${viewModel.showAddDebtSheet}")
        println("DEBUG: currentUserCode = ${viewModel.currentUserCode}")
        println("DEBUG: selectedRoommates = ${viewModel.selectedRoommates}")
        
        coEvery { ClientSingleton.portfolioApi.createDebt(any()) } returns Response.success("OK")

        viewModel.createDebt(fintoToken)
        advanceUntilIdle()

        println("DEBUG: isAddingDebt after = ${viewModel.isAddingDebt}")
        println("DEBUG: showAddDebtSheet after = ${viewModel.showAddDebtSheet}")
    }

    @Test
    fun `createDebt does nothing if amount is invalid or no roommate selected`() = runTest {
        val fintoToken = "token_di_prova"

        viewModel.openAddDebtSheet()

        // Caso importo invalido
        viewModel.updateNewDebtAmount("0")
        viewModel.updateNewDebtTitle("Spesa")
        viewModel.toggleRoommateSelection("user_2")
        viewModel.createDebt(fintoToken)
        advanceUntilIdle()
        assertTrue(viewModel.showAddDebtSheet)

        // Caso nessun coinquilino
        viewModel.updateNewDebtAmount("100")
        viewModel.toggleRoommateSelection("user_2") // Deseleziona
        viewModel.createDebt(fintoToken)
        advanceUntilIdle()
        assertTrue(viewModel.showAddDebtSheet)
    }

    @Test
    fun `createDebt with non-included current user calculates correct split amount`() = runTest {
        val userToken = "my_token"

        viewModel.openAddDebtSheet()
        viewModel.updateNewDebtAmount("100.0")
        viewModel.updateNewDebtTitle("Bolletta")
        viewModel.updateNewDebtCategory(DebtType.BILL)

        viewModel.toggleRoommateSelection("friend_1")
        viewModel.toggleRoommateSelection("friend_2")

        coEvery { ClientSingleton.portfolioApi.createDebt(any()) } returns Response.success("NEW_DEBT_ID")
        coEvery { portfolioRepo.fetchTotalDebt(any()) } returns Result.success(0.0f)
        coEvery { portfolioRepo.fetchTotalCredits(any()) } returns Result.success(0.0f)
        coEvery { portfolioRepo.fetchUserPortfolio(any()) } returns Result.success(PortfolioDTO(debts = emptyList<DebtDTO>()))
        coEvery { portfolioRepo.fetchUserProfile(any()) } returns Result.success(UserAccountDTO(userCode = userToken))

        viewModel.loadPortfolio(userToken)
        advanceUntilIdle()

        viewModel.openAddDebtSheet()
        viewModel.updateNewDebtAmount("100.0")
        viewModel.updateNewDebtTitle("Bolletta")
        viewModel.updateNewDebtCategory(DebtType.BILL)

        viewModel.toggleRoommateSelection("friend_1")
        viewModel.toggleRoommateSelection("friend_2")

        viewModel.createDebt(userToken)
        advanceUntilIdle()

        val expectedAmount = (100.0 / 3).toFloat()

        coVerify(atLeast = 1) {
            ClientSingleton.portfolioApi.createDebt(match {
                it.receiverUserCode == "friend_1" &&
                        Math.abs(it.amount - expectedAmount) < 0.01
            })
        }

        coVerify(atLeast = 1) {
            ClientSingleton.portfolioApi.createDebt(match {
                it.receiverUserCode == "friend_2" &&
                        Math.abs(it.amount - expectedAmount) < 0.01
            })
        }
    }

    @Test
    fun `settleDebt calls repository and reloads portfolio`() = runTest {
        val token = "token"
        // Setup per inizializzare currentUserCode se necessario (anche se settleDebt non lo usa direttamente per la chiamata repo, loadPortfolio sì)
        setupSuccessfulLoad(token)
        
        coEvery { portfolioRepo.settleDebt("debt_id") } returns Result.success(Unit)

        viewModel.settleDebt(token, "debt_id")
        
        // Non controlliamo isSettlingDebt qui perché potrebbe cambiare troppo velocemente
        advanceUntilIdle()

        assertFalse(viewModel.isSettlingDebt)
        coVerify(atLeast = 1) { portfolioRepo.settleDebt("debt_id") }
    }
}
