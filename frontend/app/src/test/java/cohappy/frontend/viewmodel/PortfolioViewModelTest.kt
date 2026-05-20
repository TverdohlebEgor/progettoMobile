package cohappy.frontend.viewmodel

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.enum.DebtType
import cohappy.frontend.client.dto.response.DebtDTO
import cohappy.frontend.client.dto.response.GetHouseDTO
import cohappy.frontend.client.dto.response.PortfolioDTO
import cohappy.frontend.client.dto.response.UserAccountDTO
import cohappy.frontend.repository.PortfolioRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class PortfolioViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: PortfolioRepository
    private lateinit var viewModel: PortfolioViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        
        // Mock ClientSingleton to prevent real network calls and handle direct API usage in VM
        mockkObject(ClientSingleton)
        every { ClientSingleton.houseApi } returns mockk(relaxed = true)
        every { ClientSingleton.userApi } returns mockk(relaxed = true)
        every { ClientSingleton.portfolioApi } returns mockk(relaxed = true)

        viewModel = PortfolioViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    /* ########################################
                 LOAD PORTFOLIO
     ########################################*/

    @Test
    fun `loadPortfolio happy path maps debts and credits correctly with split logic`() = runTest {
        val token = "ME"
        
        // Mock Totals
        coEvery { repository.fetchTotalDebt(any()) } returns Result.success(100f)
        coEvery { repository.fetchTotalCredits(any()) } returns Result.success(200f)
        
        // Mock profile and house to populate roommates list
        val myProfile = UserAccountDTO(userCode = "ME", houseCode = "H1")
        coEvery { repository.fetchUserProfile(any()) } returns Result.success(myProfile)
        
        val house = GetHouseDTO(houseCode = "H1", admins = listOf("ME"), users = listOf("OTHER_1"))
        coEvery { ClientSingleton.houseApi.getHouse("H1") } returns Response.success(house)
        
        val otherProfile = UserAccountDTO(userCode = "OTHER_1", name = "Mario", surname = "Rossi")
        coEvery { ClientSingleton.userApi.getUserProfile("OTHER_1") } returns Response.success(otherProfile)

        // Mock Portfolio with two items:
        // 1. A debt I owe: Total 50, participants ME + OTHER_1 (creator). My share = 25.
        val debtIOwe = DebtDTO(
            debtId = "D1",
            creditorUserCode = "OTHER_1",
            debtorsUserCode = mapOf("ME" to false),
            amount = 50f,
            description = "Pizza",
            debtType = DebtType.GROCERIE,
            isCreatorIncluded = true
        )
        // 2. A credit I have: Total 100, participant OTHER_1. Creator (ME) included. Share = 50.
        val creditIHave = DebtDTO(
            debtId = "C1",
            creditorUserCode = "ME",
            debtorsUserCode = mapOf("OTHER_1" to false),
            amount = 100f,
            description = "Bolletta",
            debtType = DebtType.BILL,
            isCreatorIncluded = true
        )
        
        coEvery { repository.fetchUserPortfolio(any()) } returns Result.success(PortfolioDTO(debts = listOf(debtIOwe, creditIHave)))

        viewModel.loadPortfolio(token)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify Totals
        assertEquals(100.0, viewModel.totalDebts, 0.1)
        assertEquals(200.0, viewModel.totalCredits, 0.1)
        
        // Verify Transactions (reversed list, so C1 is first)
        assertEquals(2, viewModel.transactions.size)
        
        val txCredit = viewModel.transactions[0] // Credit because it was last in list
        assertFalse(txCredit.isDebt)
        assertEquals(100.0, txCredit.amount, 0.1) // 100 / 1 (only OTHER_1 in debtors map)
        assertEquals("Credito verso Mario Rossi", txCredit.subtitle)

        val txDebt = viewModel.transactions[1]
        assertTrue(txDebt.isDebt)
        assertEquals(50.0, txDebt.amount, 0.1) // 50 / 1 (only ME in debtors map)
        assertEquals("Devi a Mario Rossi", txDebt.subtitle)
        
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun `loadPortfolio handles partial failures in repository gracefully`() = runTest {
        // If profile fetch fails, it should still try to load portfolio but might have empty roommate names
        coEvery { repository.fetchUserProfile(any()) } returns Result.failure(Exception("Profile Error"))
        coEvery { repository.fetchUserPortfolio(any()) } returns Result.success(PortfolioDTO(debts = emptyList()))
        
        viewModel.loadPortfolio("ME")
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0.0, viewModel.totalDebts, 0.1)
        assertTrue(viewModel.transactions.isEmpty())
        assertFalse(viewModel.isLoading)
    }

    /* ########################################
                 CREATE DEBT
     ########################################*/

    @Test
    fun `createDebt success triggers API and reloads portfolio`() = runTest {
        // Setup initial user code
        coEvery { repository.fetchUserProfile(any()) } returns Result.success(UserAccountDTO(userCode = "ME"))
        coEvery { repository.fetchUserPortfolio(any()) } returns Result.success(PortfolioDTO(debts = emptyList()))
        coEvery { repository.fetchTotalDebt(any()) } returns Result.success(0f)
        coEvery { repository.fetchTotalCredits(any()) } returns Result.success(0f)
        
        viewModel.loadPortfolio("ME")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateNewDebtTitle("Spesa Settimanale")
        viewModel.updateNewDebtAmount("120.50")
        viewModel.toggleRoommateSelection("OTHER_1")
        viewModel.toggleRoommateSelection("ME") // I participate too
        
        coEvery { ClientSingleton.portfolioApi.createDebt(any()) } returns Response.success("OK")

        viewModel.createDebt("ME")
        
        // Check "Adding" state while running
        assertTrue(viewModel.isAddingDebt)
        
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { 
            ClientSingleton.portfolioApi.createDebt(match { 
                it.description == "Spesa Settimanale" && 
                it.amount == 60.25f && // 120.5 / 2 participants = 60.25 share each. amount stored is total - creator's share = 60.25
                it.isCreatorIncluded == true &&
                it.receiverCode.containsKey("OTHER_1") &&
                !it.receiverCode.containsKey("ME")
            }) 
        }
        
        assertFalse(viewModel.isAddingDebt)
        assertFalse(viewModel.showAddDebtSheet)
        // Verify reload (once for loadPortfolio initial, once after createDebt)
        coVerify(exactly = 2) { repository.fetchUserPortfolio(any()) }
    }

    @Test
    fun `createDebt prevents concurrent calls (anti-spam)`() = runTest {
        coEvery { repository.fetchUserProfile(any()) } returns Result.success(UserAccountDTO(userCode = "ME"))
        coEvery { repository.fetchUserPortfolio(any()) } returns Result.success(PortfolioDTO(debts = emptyList()))
        coEvery { repository.fetchTotalDebt(any()) } returns Result.success(0f)
        coEvery { repository.fetchTotalCredits(any()) } returns Result.success(0f)
        
        viewModel.loadPortfolio("ME")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateNewDebtTitle("Spesa")
        viewModel.updateNewDebtAmount("10")
        viewModel.toggleRoommateSelection("OTHER")
        
        coEvery { ClientSingleton.portfolioApi.createDebt(any()) } coAnswers {
            kotlinx.coroutines.delay(1000)
            Response.success("OK")
        }

        // Call twice
        viewModel.createDebt("ME")
        viewModel.createDebt("ME")

        testDispatcher.scheduler.advanceUntilIdle()

        // Should only have started one
        coVerify(exactly = 1) { ClientSingleton.portfolioApi.createDebt(any()) }
    }

    /* ########################################
                 SETTLE DEBT
     ########################################*/

    @Test
    fun `settleDebt calls patchDebtPaid and refreshes on success`() = runTest {
        // Mock all dependencies for loadPortfolio
        coEvery { repository.fetchUserProfile(any()) } returns Result.success(UserAccountDTO(userCode = "ME"))
        coEvery { repository.fetchTotalDebt(any()) } returns Result.success(0f)
        coEvery { repository.fetchTotalCredits(any()) } returns Result.success(0f)
        coEvery { repository.fetchUserPortfolio(any()) } returns Result.success(PortfolioDTO(debts = emptyList()))

        viewModel.loadPortfolio("ME")
        testDispatcher.scheduler.advanceUntilIdle()

        val debtId = "D1"
        coEvery { repository.patchDebtPaid(any(), any(), any()) } returns Result.success(Unit)
        
        viewModel.settleDebt("ME", debtId)
        
        // Check state before advancing
        assertTrue("isSettlingDebt should be true after calling settleDebt", viewModel.isSettlingDebt)

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.patchDebtPaid(debtId, any(), true) }
        // 1 from initial loadPortfolio, 1 from settleDebt reload
        coVerify(exactly = 2) { repository.fetchUserPortfolio(any()) }
        assertFalse("isSettlingDebt should be false after completion", viewModel.isSettlingDebt)
    }

    /* ########################################
                 FILTERING
     ########################################*/

    @Test
    fun `getFilteredTransactions returns correct subset based on filter`() = runTest {
        // Since we can't easily set 'transactions' directly (private setter), we mock loadPortfolio results
        val mockDebts = listOf(
            DebtDTO(debtId="1", creditorUserCode="OTHER", debtorsUserCode=mapOf("ME" to false), amount=10f, debtType=DebtType.OTHER),
            DebtDTO(debtId="2", creditorUserCode="ME", debtorsUserCode=mapOf("OTHER" to false), amount=20f, debtType=DebtType.OTHER)
        )
        coEvery { repository.fetchUserProfile(any()) } returns Result.success(UserAccountDTO(userCode = "ME"))
        coEvery { repository.fetchUserPortfolio(any()) } returns Result.success(PortfolioDTO(debts = mockDebts))
        coEvery { repository.fetchTotalDebt(any()) } returns Result.success(0f)
        coEvery { repository.fetchTotalCredits(any()) } returns Result.success(0f)

        viewModel.loadPortfolio("ME")
        testDispatcher.scheduler.advanceUntilIdle()

        // Default ALL
        assertEquals(2, viewModel.getFilteredTransactions().size)

        // DEBTS
        viewModel.setFilter("DEBTS")
        val filteredDebts = viewModel.getFilteredTransactions()
        assertEquals(1, filteredDebts.size)
        assertTrue(filteredDebts[0].isDebt)

        // CREDITS
        viewModel.setFilter("CREDITS")
        val filteredCredits = viewModel.getFilteredTransactions()
        assertEquals(1, filteredCredits.size)
        assertFalse(filteredCredits[0].isDebt)
    }
}
