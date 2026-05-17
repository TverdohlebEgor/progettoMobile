package cohappy.frontend.viewmodel

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.HouseApiClient
import cohappy.frontend.client.PortfolioApiClient
import cohappy.frontend.client.UserApiClient
import cohappy.frontend.client.dto.enum.DebtType
import cohappy.frontend.client.dto.request.CreateDebtDTO
import cohappy.frontend.client.dto.response.DebtDTO
import cohappy.frontend.client.dto.response.GetHouseDTO
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
    private val mockRepository = mockk<PortfolioRepository>()
    private val testDispatcher = StandardTestDispatcher()

    private val mockUserApi = mockk<UserApiClient>()
    private val mockHouseApi = mockk<HouseApiClient>()
    private val mockPortfolioApi = mockk<PortfolioApiClient>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkObject(ClientSingleton)
        ClientSingleton.userApi = mockUserApi
        ClientSingleton.houseApi = mockHouseApi
        ClientSingleton.portfolioApi = mockPortfolioApi

        viewModel = PortfolioViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    // --- HAPPY PATHS ---

    @Test
    fun `loadPortfolio - Happy Path - Carica totali, coinquilini e debiti`() = runTest {
        val userToken = "MY_TOKEN"
        val myProfile = UserAccountDTO(userCode = "MY_TOKEN", name = "Regina", houseCode = "CASA1")
        val houseMock = GetHouseDTO(users = listOf("USER2"))
        val user2Profile = UserAccountDTO(userCode = "USER2", name = "Suddito")
        val mockDebt = DebtDTO(
            debtId = "D1", debtorsUserCode = mapOf("MY_TOKEN" to false), creditorUserCode = "USER2",
            amount = 50.0f, description = "Cena", debtType = DebtType.DELIVERY_AND_EATING_OUT
        )
        val mockPortfolio = PortfolioDTO(debts = listOf(mockDebt))

        coEvery { mockRepository.fetchTotalDebt(userToken) } returns Result.success(50.0f)
        coEvery { mockRepository.fetchTotalCredits(userToken) } returns Result.success(10.0f)
        coEvery { mockRepository.fetchUserProfile(userToken) } returns Result.success(myProfile)
        coEvery { mockHouseApi.getHouse("CASA1") } returns Response.success(houseMock)
        coEvery { mockUserApi.getUserProfile("USER2") } returns Response.success(user2Profile)
        coEvery { mockRepository.fetchUserPortfolio(userToken) } returns Result.success(mockPortfolio)

        viewModel.loadPortfolio(userToken)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertEquals(50.0, viewModel.totalDebts, 0.0)
        assertEquals(10.0, viewModel.totalCredits, 0.0)
        assertEquals(2, viewModel.availableRoommates.size) // Tu + Suddito
        assertEquals(1, viewModel.transactions.size)
        assertTrue(viewModel.transactions[0].isDebt)
        assertEquals("Suddito ", viewModel.transactions[0].beneficiaryName)
    }

    @Test
    fun `createDebt - Happy Path - Calcola split corretto e crea debiti`() = runTest {
        // Setup base fittizio
        viewModel.updateNewDebtTitle("Bolletta Luce")
        viewModel.updateNewDebtAmount("100.0")
        viewModel.updateNewDebtCategory(DebtType.BILL)

        // Moka lo stato interno
        val myCode = "MY_TOKEN"
        coEvery { mockRepository.fetchTotalDebt(any()) } returns Result.success(0f)
        coEvery { mockRepository.fetchTotalCredits(any()) } returns Result.success(0f)
        coEvery { mockRepository.fetchUserProfile(any()) } returns Result.success(UserAccountDTO(userCode = myCode))
        coEvery { mockRepository.fetchUserPortfolio(any()) } returns Result.success(PortfolioDTO())

        viewModel.loadPortfolio(myCode)
        advanceUntilIdle()

        // Seleziona coinquilini (Tu + un altro) -> Divisore = 2, Quota = 50.0
        viewModel.toggleRoommateSelection("USER2")
        viewModel.toggleRoommateSelection(myCode)

        coEvery { mockPortfolioApi.createDebt(any()) } returns Response.success("OK")

        viewModel.createDebt(myCode)
        advanceUntilIdle()

        val slot = slot<CreateDebtDTO>()
        coVerify(exactly = 1) { mockPortfolioApi.createDebt(capture(slot)) }

        assertEquals(myCode, slot.captured.creditorCode)
        assertEquals(mapOf("USER2" to false), slot.captured.receiverCode)
        assertEquals(50.0f, slot.captured.amount)
    }

    // --- BAD PATHS ---

    @Test
    fun `loadPortfolio - Bad Path - Gestisce fallimento chiamate API resettando i valori`() = runTest {
        coEvery { mockRepository.fetchTotalDebt(any()) } returns Result.failure(Exception("Error"))
        coEvery { mockRepository.fetchTotalCredits(any()) } returns Result.failure(Exception("Error"))
        coEvery { mockRepository.fetchUserProfile(any()) } returns Result.failure(Exception("Error"))
        coEvery { mockRepository.fetchUserPortfolio(any()) } returns Result.failure(Exception("Error"))

        viewModel.loadPortfolio("TOKEN")
        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertEquals(0.0, viewModel.totalDebts, 0.0)
        assertEquals(0.0, viewModel.totalCredits, 0.0)
        assertTrue(viewModel.transactions.isEmpty())
        assertEquals(0, viewModel.availableRoommates.size)
    }

    @Test
    fun `createDebt - Bad Path - Previene spam click (isAddingDebt)`() = runTest {
        viewModel.updateNewDebtTitle("Spam")
        viewModel.updateNewDebtAmount("50.0")
        viewModel.toggleRoommateSelection("USER2")

        // Simulo una chiamata lenta
        coEvery { mockPortfolioApi.createDebt(any()) } coAnswers {
            kotlinx.coroutines.delay(1000)
            Response.success("OK")
        }

        // Chiamo due volte velocemente
        viewModel.createDebt("MY_TOKEN")
        viewModel.createDebt("MY_TOKEN")

        advanceUntilIdle()

        // Deve aver chiamato l'API solo 1 volta grazie al blocco isAddingDebt
        coVerify(exactly = 1) { mockPortfolioApi.createDebt(any()) }
    }
}