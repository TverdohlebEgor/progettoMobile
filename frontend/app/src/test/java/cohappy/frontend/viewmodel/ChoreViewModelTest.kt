package cohappy.frontend.viewmodel

import android.util.Log
import cohappy.frontend.client.dto.request.CreateChoreDTO
import cohappy.frontend.client.dto.response.GetChoreDTO
import cohappy.frontend.client.dto.response.GetHouseDTO
import cohappy.frontend.client.dto.response.UserAccountDTO
import cohappy.frontend.repository.ChoreRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class ChoreViewModelTest {

    private lateinit var viewModel: ChoreViewModel
    private lateinit var repository: ChoreRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0

        repository = mockk(relaxed = true)
        
        // Setup di default per evitare che i job in background (pallini calendar) consumino i mock specifici
        coEvery { repository.fetchChores(any(), any()) } returns Response.success(emptyList())
        coEvery { repository.getUserProfile(any()) } returns Response.success(UserAccountDTO())
        coEvery { repository.getHouseDetails(any()) } returns Response.success(GetHouseDTO())

        viewModel = ChoreViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
        unmockkAll()
    }

    @Test
    fun `loadUserData - Successo caricamento profilo e houseCode`() = runTest {
        val mockUser = UserAccountDTO(name = "Max", userCode = "U1", houseCode = "H1")
        coEvery { repository.getUserProfile("token") } returns Response.success(mockUser)
        coEvery { repository.getHouseDetails("H1") } returns Response.success(GetHouseDTO(houseCode = "H1"))

        viewModel.loadUserData("token")
        advanceUntilIdle()

        assertEquals("Max", viewModel.nomeUtente)
        assertEquals("H1", viewModel.houseCode)
        assertEquals("U1", viewModel.currentUserCode)
    }

    @Test
    fun `loadUserData - Gestione errore API profilo`() = runTest {
        coEvery { repository.getUserProfile("token") } returns Response.error(404, "".toResponseBody())

        viewModel.loadUserData("token")
        advanceUntilIdle()

        assertEquals("Errore API", viewModel.nomeUtente)
        assertTrue(viewModel.chores.isEmpty())
    }

    @Test
    fun `onDateSelected - Aggiorna data e ricarica faccende`() = runTest {
        val newDate = LocalDate.of(2024, 12, 25)
        val mockUser = UserAccountDTO(userCode = "U1", houseCode = "H1")
        
        coEvery { repository.getUserProfile("token") } returns Response.success(mockUser)
        coEvery { repository.fetchChores("H1", newDate) } returns Response.success(listOf(
            GetChoreDTO(choreCode = "C1", name = "Chore Natale")
        ))

        viewModel.loadUserData("token")
        advanceUntilIdle()
        
        viewModel.onDateSelected(newDate, "token")
        advanceUntilIdle()

        assertEquals(newDate, viewModel.selectedDate)
        assertEquals(1, viewModel.chores.size)
        assertEquals("Chore Natale", viewModel.chores[0].title)
    }

    @Test
    fun `toggleChoreCompletion - Successo e aggiornamento ottimistico`() = runTest {
        val today = LocalDate.now()
        val mockUser = UserAccountDTO(userCode = "U1", houseCode = "H1")
        val mockChore = GetChoreDTO(choreCode = "C1", completed = false, assignedTo = "U1")
        
        coEvery { repository.getUserProfile("token") } returns Response.success(mockUser)
        coEvery { repository.fetchChores("H1", today) } returns Response.success(listOf(mockChore))
        
        viewModel.loadUserData("token")
        advanceUntilIdle()

        coEvery { repository.updateChoreStatus(any()) } returns Response.success("OK")
        coEvery { repository.fetchChores("H1", today) } returns Response.success(listOf(mockChore.copy(completed = true)))

        viewModel.toggleChoreCompletion("C1", "U1", true)
        
        // Verifica immediata dell'ottimismo
        assertTrue("Aggiornamento ottimistico fallito", viewModel.chores[0].isCompleted)
        
        advanceUntilIdle()
        assertTrue("Stato finale dopo refresh deve essere true", viewModel.chores[0].isCompleted)
    }

    @Test
    fun `toggleChoreCompletion - Rollback in caso di errore`() = runTest {
        val today = LocalDate.now()
        val mockUser = UserAccountDTO(userCode = "U1", houseCode = "H1")
        val mockChore = GetChoreDTO(choreCode = "C1", completed = false)
        
        coEvery { repository.getUserProfile("token") } returns Response.success(mockUser)
        coEvery { repository.fetchChores("H1", today) } returns Response.success(listOf(mockChore))
        
        viewModel.loadUserData("token")
        advanceUntilIdle()

        coEvery { repository.updateChoreStatus(any()) } returns Response.error(500, "".toResponseBody())

        viewModel.toggleChoreCompletion("C1", "U1", true)
        advanceUntilIdle()

        assertFalse("Rollback a false fallito dopo errore API", viewModel.chores[0].isCompleted)
    }

    @Test
    fun `createChore - Espansione 6 mesi per ricorsiva`() = runTest {
        val startDate = LocalDate.of(2025, 1, 1)
        val mockUser = UserAccountDTO(userCode = "U1", houseCode = "H1")
        val slot = slot<CreateChoreDTO>()
        
        coEvery { repository.getUserProfile(any()) } returns Response.success(mockUser)
        coEvery { repository.createChore(capture(slot)) } returns Response.success("OK")

        viewModel.loadUserData("token")
        advanceUntilIdle()

        viewModel.createChore("token", "Pulizia", "Desc", listOf(startDate), null, true) {}
        advanceUntilIdle()

        val captured = slot.captured
        assertNotNull(captured.days)
        // 6 mesi = 26 settimane circa
        assertTrue("Dovrebbero esserci circa 26 date, trovate: ${captured.days!!.size}", captured.days!!.size >= 26)
        assertTrue("Le ricorsive devono mandare assignedTo vuoto (house chores)", captured.assignedTo?.isEmpty() == true)
    }

    @Test
    fun `loadHouseRoommates - Unione corretta admin e utenti`() = runTest {
        val mockHouse = GetHouseDTO(houseCode = "H1", admins = listOf("A1"), users = listOf("U1"))
        coEvery { repository.getUserProfile("token") } returns Response.success(UserAccountDTO(houseCode = "H1"))
        coEvery { repository.getHouseDetails("H1") } returns Response.success(mockHouse)
        coEvery { repository.getUserProfile("A1") } returns Response.success(UserAccountDTO(name = "AdminName", userCode = "A1"))
        coEvery { repository.getUserProfile("U1") } returns Response.success(UserAccountDTO(name = "UserName", userCode = "U1"))

        viewModel.loadUserData("token")
        advanceUntilIdle()

        val names = viewModel.roommates.map { it.second }
        assertEquals(2, names.size)
        assertTrue(names.contains("AdminName"))
        assertTrue(names.contains("UserName"))
    }

    @Test
    fun `refreshChoresInternal - Mappatura Te per utente corrente`() = runTest {
        val today = LocalDate.now()
        val mockChore = GetChoreDTO(choreCode = "C1", assignedTo = "U1")
        
        coEvery { repository.getUserProfile("token") } returns Response.success(UserAccountDTO(userCode = "U1", houseCode = "H1"))
        coEvery { repository.fetchChores("H1", today) } returns Response.success(listOf(mockChore))

        viewModel.loadUserData("token")
        advanceUntilIdle()

        assertEquals("Te", viewModel.chores[0].assigneeName)
    }

    @Test
    fun `refreshChoresInternal - Mappatura nome coinquilino se non Te`() = runTest {
        val today = LocalDate.now()
        val mockChore = GetChoreDTO(choreCode = "C1", assignedTo = "U2")
        
        coEvery { repository.getUserProfile("token") } returns Response.success(UserAccountDTO(userCode = "U1", houseCode = "H1"))
        coEvery { repository.getHouseDetails("H1") } returns Response.success(GetHouseDTO(users = listOf("U2")))
        coEvery { repository.getUserProfile("U2") } returns Response.success(UserAccountDTO(name = "Marco", userCode = "U2"))
        coEvery { repository.fetchChores("H1", today) } returns Response.success(listOf(mockChore))

        viewModel.loadUserData("token")
        advanceUntilIdle()

        assertEquals("Marco", viewModel.chores[0].assigneeName)
    }

    @Test
    fun `loadDaysWithChores - Riempie la lista dei pallini del calendario`() = runTest {
        val today = LocalDate.now()
        coEvery { repository.getUserProfile(any()) } returns Response.success(UserAccountDTO(houseCode = "H1"))
        coEvery { repository.fetchChores("H1", today) } returns Response.success(listOf(GetChoreDTO(choreCode = "C1")))
        
        viewModel.loadUserData("token")
        advanceUntilIdle()

        assertTrue(viewModel.daysWithChores.contains(today))
    }

    @Test
    fun `createChore - Singola data con assegnatario specifico`() = runTest {
        val date = LocalDate.of(2024, 10, 10)
        val slot = slot<CreateChoreDTO>()
        
        coEvery { repository.getUserProfile(any()) } returns Response.success(UserAccountDTO(userCode = "U1", houseCode = "H1"))
        coEvery { repository.createChore(capture(slot)) } returns Response.success("OK")

        viewModel.loadUserData("token")
        advanceUntilIdle()

        viewModel.createChore("token", "Spesa", "Latte", listOf(date), "U1", false) {}
        advanceUntilIdle()

        val captured = slot.captured
        assertEquals(1, captured.days?.size)
        assertEquals("U1", captured.assignedTo?.get(date))
    }
}
