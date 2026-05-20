package cohappy.frontend.viewmodel

import android.util.Log
import cohappy.frontend.client.dto.request.CreateChoreDTO
import cohappy.frontend.client.dto.request.PatchChoreDTO
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

        // Setup di default base per i mock
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

    // --- TEST UTENTE E CASA (loadUserData) ---

    @Test
    fun `loadUserData - Successo caricamento profilo e houseCode (Happy Path)`() = runTest {
        val mockUser = UserAccountDTO(name = "Max", userCode = "U1", houseCode = "H1")
        coEvery { repository.getUserProfile("token") } returns Response.success(mockUser)
        coEvery { repository.getHouseDetails("H1") } returns Response.success(GetHouseDTO(houseCode = "H1"))

        viewModel.loadUserData("token")
        advanceUntilIdle()

        assertEquals("Max", viewModel.nomeUtente)
        assertEquals("H1", viewModel.houseCode)
        assertEquals("U1", viewModel.currentUserCode)
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun `loadUserData - Utente senza casa svuota la lista (Happy Path)`() = runTest {
        val mockUser = UserAccountDTO(name = "Max", userCode = "U1", houseCode = null)
        coEvery { repository.getUserProfile("token") } returns Response.success(mockUser)

        viewModel.loadUserData("token")
        advanceUntilIdle()

        assertEquals("Max", viewModel.nomeUtente)
        assertNull(viewModel.houseCode)
        assertTrue(viewModel.chores.isEmpty())
        assertTrue(viewModel.roommates.isEmpty())
    }

    @Test
    fun `loadUserData - Token vuoto o blank imposta Ospite (Bad Path)`() = runTest {
        viewModel.loadUserData("   ")
        advanceUntilIdle()

        assertEquals("Ospite", viewModel.nomeUtente)
        assertTrue(viewModel.chores.isEmpty())
    }

    @Test
    fun `loadUserData - Gestione errore API profilo (Bad Path)`() = runTest {
        coEvery { repository.getUserProfile("token") } returns Response.error(404, "".toResponseBody())

        viewModel.loadUserData("token")
        advanceUntilIdle()

        assertEquals("Errore API", viewModel.nomeUtente)
        assertTrue(viewModel.chores.isEmpty())
    }

    @Test
    fun `loadUserData - Eccezione di rete imposta Offline (Bad Path)`() = runTest {
        coEvery { repository.getUserProfile(any()) } throws RuntimeException("Network Error")

        viewModel.loadUserData("token")
        advanceUntilIdle()

        assertEquals("Offline", viewModel.nomeUtente)
        assertTrue(viewModel.chores.isEmpty())
    }

    // --- TEST SELEZIONE DATA E CALENDARIO (onDateSelected) ---

    @Test
    fun `onDateSelected - Aggiorna data e ricarica faccende (Happy Path)`() = runTest {
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
    fun `onDateSelected - Senza houseCode ricarica i dati utente (Bad Path Gestito)`() = runTest {
        val mockUser = UserAccountDTO(name = "Max", userCode = "U1", houseCode = "H1")
        coEvery { repository.getUserProfile("token") } returns Response.success(mockUser)

        // Allo stato iniziale houseCode è null. Chiamare onDateSelected deve scatenare loadUserData
        viewModel.onDateSelected(LocalDate.now(), "token")
        advanceUntilIdle()

        assertEquals("Max", viewModel.nomeUtente)
    }

    @Test
    fun `onDateSelected - Cambio mese ricarica il calendario (Happy Path)`() = runTest {
        val oldDate = LocalDate.of(2024, 11, 15)
        val newDate = LocalDate.of(2024, 12, 1)
        val mockUser = UserAccountDTO(userCode = "U1", houseCode = "H1")

        coEvery { repository.getUserProfile(any()) } returns Response.success(mockUser)
        coEvery { repository.getHouseDetails(any()) } returns Response.success(GetHouseDTO(houseCode = "H1"))

        viewModel.loadUserData("token")
        advanceUntilIdle()

        viewModel.onDateSelected(oldDate, "token")
        advanceUntilIdle()

        viewModel.onDateSelected(newDate, "token")
        advanceUntilIdle()

        // Verifica che fetchChores per il calendario (start date = inizio mese - 7 giorni) sia stato chiamato per la nuova data
        val expectedCalendarStart = newDate.withDayOfMonth(1).minusDays(7)
        coVerify { repository.fetchChores("H1", expectedCalendarStart) }
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

    // --- TEST COINQUILINI ED ASSEGNAZIONI (refreshChoresInternal & loadHouseRoommates) ---

    @Test
    fun `loadHouseRoommates - Unione corretta admin e utenti (Happy Path)`() = runTest {
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
    fun `refreshChoresInternal - Eccezione mantiene dati esistenti (Bad Path)`() = runTest {
        val today = LocalDate.now()
        val mockChore = GetChoreDTO(choreCode = "C1", name = "TestChore")

        coEvery { repository.getUserProfile("token") } returns Response.success(UserAccountDTO(userCode = "U1", houseCode = "H1"))
        coEvery { repository.fetchChores("H1", today) } returns Response.success(listOf(mockChore))

        viewModel.loadUserData("token")
        advanceUntilIdle()
        assertEquals(1, viewModel.chores.size)

        // Ora l'API fallisce per un refresh
        coEvery { repository.fetchChores("H1", today) } throws RuntimeException("Errore di rete temporaneo")

        viewModel.onDateSelected(today, "token")
        advanceUntilIdle()

        // I dati vecchi devono rimanere intatti
        assertEquals(1, viewModel.chores.size)
        assertEquals("TestChore", viewModel.chores[0].title)
    }

    // --- TEST MODIFICA STATO (toggleChoreCompletion) ---

    @Test
    fun `toggleChoreCompletion - Successo e aggiornamento ottimistico (Happy Path)`() = runTest {
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

        // Verifica ottimistica
        assertTrue("Aggiornamento ottimistico fallito", viewModel.chores[0].isCompleted)

        advanceUntilIdle()
        assertTrue("Stato finale deve rimanere true", viewModel.chores[0].isCompleted)
    }

    @Test
    fun `toggleChoreCompletion - Invia la data corretta nel payload della patch`() = runTest {
        val today = LocalDate.now()
        val slot = slot<PatchChoreDTO>()
        val mockUser = UserAccountDTO(userCode = "U1", houseCode = "H1")

        coEvery { repository.getUserProfile(any()) } returns Response.success(mockUser)
        coEvery { repository.updateChoreStatus(capture(slot)) } returns Response.success("OK")

        viewModel.loadUserData("token")
        advanceUntilIdle()

        // Selezioniamo una data specifica diversa da oggi
        val selectedDate = today.plusDays(5)
        viewModel.onDateSelected(selectedDate, "token")
        advanceUntilIdle()

        viewModel.toggleChoreCompletion("CHORE_123", "U1", true)
        advanceUntilIdle()

        // Verifica che la patch colpisca la data selezionata
        assertEquals("La patch deve contenere la data selezionata", selectedDate, slot.captured.day)
        assertEquals("CHORE_123", slot.captured.choreCode)
        assertEquals("U1", slot.captured.assignedTo)
        assertEquals(true, slot.captured.completed)
    }

    @Test
    fun `toggleChoreCompletion - Rollback in caso di errore (Bad Path)`() = runTest {
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

        // Rollback a false post errore API
        assertFalse("Rollback a false fallito dopo errore API", viewModel.chores[0].isCompleted)
    }

    // --- TEST CREAZIONE FACCENDE (createChore) ---

    @Test
    fun `createChore - Singola data senza assegnatario usa null (Faccenda di casa)`() = runTest {
        val date = LocalDate.of(2024, 10, 10)
        val slot = slot<CreateChoreDTO>()

        coEvery { repository.getUserProfile(any()) } returns Response.success(UserAccountDTO(userCode = "U1", houseCode = "H1"))
        coEvery { repository.createChore(capture(slot)) } returns Response.success("OK")

        viewModel.loadUserData("token")
        advanceUntilIdle()

        viewModel.createChore("token", "Spesa", "Latte", listOf(date), null, false) {}
        advanceUntilIdle()

        val captured = slot.captured
        assertNull("La mappa deve contenere null per faccende non assegnate", 
            captured.assignedTo?.get(date))
    }

    @Test
    fun `createChore - Singola data con assegnatario specifico (Happy Path)`() = runTest {
        val date = LocalDate.of(2024, 10, 10)
        val slot = slot<CreateChoreDTO>()

        coEvery { repository.getUserProfile(any()) } returns Response.success(UserAccountDTO(userCode = "U1", houseCode = "H1"))
        coEvery { repository.createChore(capture(slot)) } returns Response.success("OK")

        viewModel.loadUserData("token")
        advanceUntilIdle()

        var successCalled = false
        viewModel.createChore("token", "Spesa", "Latte", listOf(date), "U1", false) {
            successCalled = true
        }
        advanceUntilIdle()

        assertTrue(successCalled)
        val captured = slot.captured
        assertEquals(1, captured.days?.size)
        assertEquals("U1", captured.assignedTo?.get(date))
        assertFalse(viewModel.isLoading)
    }

    @Test
    fun `createChore - Espansione 6 mesi per ricorsiva ignora assegnatario e usa null`() = runTest {
        val startDate = LocalDate.of(2025, 1, 1)
        val mockUser = UserAccountDTO(userCode = "U1", houseCode = "H1")
        val slot = slot<CreateChoreDTO>()

        coEvery { repository.getUserProfile(any()) } returns Response.success(mockUser)
        coEvery { repository.createChore(capture(slot)) } returns Response.success("OK")

        viewModel.loadUserData("token")
        advanceUntilIdle()

        // Passiamo un assegnatario "U2", ma essendo ricorsiva deve essere ignorato
        viewModel.createChore("token", "Pulizia", "Desc", listOf(startDate), "U2", true) {}
        advanceUntilIdle()

        val captured = slot.captured
        assertNotNull(captured.days)
        assertTrue("Le ricorsive devono avere assegnazioni a null anche se viene passato un utente",
            captured.assignedTo?.values?.all { it == null } == true)
    }

    @Test
    fun `createChore - Eccezione gestita disattiva isLoading e no onSuccess (Bad Path)`() = runTest {
        coEvery { repository.getUserProfile(any()) } returns Response.success(UserAccountDTO(userCode = "U1", houseCode = "H1"))
        coEvery { repository.createChore(any()) } throws RuntimeException("Timeout Server")

        viewModel.loadUserData("token")
        advanceUntilIdle()

        var successCalled = false
        viewModel.createChore("token", "P", "D", listOf(LocalDate.now()), null, false) {
            successCalled = true
        }
        advanceUntilIdle()

        assertFalse("Il callback onSuccess non deve essere chiamato se la chiamata fallisce", successCalled)
        assertFalse("isLoading deve tornare a false dopo l'errore", viewModel.isLoading)
    }

    @Test
    fun `createChore - Ricorsiva singola data genera intervalli esatti di 7 giorni per 6 mesi`() = runTest {
        // Arrange
        // Selezioniamo un giorno specifico (Lunedì 1 Gennaio 2024)
        val startDate = LocalDate.of(2024, 1, 1)
        val mockUser = UserAccountDTO(userCode = "U1", houseCode = "H1")
        val slot = slot<CreateChoreDTO>()

        coEvery { repository.getUserProfile(any()) } returns Response.success(mockUser)
        coEvery { repository.createChore(capture(slot)) } returns Response.success("OK")

        viewModel.loadUserData("token")
        advanceUntilIdle()

        // Act
        viewModel.createChore(
            userToken = "token",
            name = "Pulizia",
            description = "Test Ricorsione",
            dates = listOf(startDate),
            assignedTo = null,
            isRecursive = true
        ) {}
        advanceUntilIdle()

        // Assert
        val captured = slot.captured
        val generatedDays = captured.days

        assertNotNull("La lista dei giorni non deve essere nulla", generatedDays)
        assertTrue("La lista non deve essere vuota", generatedDays!!.isNotEmpty())

        val limitDate = startDate.plusMonths(6)

        // Verifica 1: Il giorno della settimana deve rimanere costante
        generatedDays.forEach { date ->
            assertEquals("Ogni data deve essere un Lunedì", startDate.dayOfWeek, date.dayOfWeek)
        }

        // Verifica 2: Distanza esatta di 1 settimana (7 giorni) tra ogni ricorrenza
        for (i in 0 until generatedDays.size - 1) {
            val current = generatedDays[i]
            val next = generatedDays[i + 1]
            assertEquals("La distanza tra due date consecutive deve essere esattamente di 1 settimana",
                current.plusWeeks(1), next)
        }

        // Verifica 3: La prima e l'ultima data
        assertEquals("La prima data deve coincidere con la selezione dell'utente", startDate, generatedDays.first())

        val lastDate = generatedDays.last()
        assertTrue("L'ultima data deve essere precedente al limite dei 6 mesi", lastDate.isBefore(limitDate))
        assertTrue("Aggiungendo un'altra settimana si deve superare il limite", !lastDate.plusWeeks(1).isBefore(limitDate))

        // Verifica 4: Assegnazione presente ma null per ogni giorno (richiesto dal backend)
        assertEquals("Ogni giorno generato deve essere presente nella mappa assegnazioni",
            generatedDays.size, captured.assignedTo?.size)
        assertTrue("Le assegnazioni per faccende ricorsive/libere devono essere null",
            captured.assignedTo?.values?.all { it == null } == true)
    }

    @Test
    fun `createChore - Ricorsiva con date multiple genera sequenze esatte per ciascun giorno`() = runTest {
        // Arrange
        // L'utente seleziona un Lunedì e un Giovedì della stessa settimana
        val monday = LocalDate.of(2024, 1, 1)
        val thursday = LocalDate.of(2024, 1, 4)
        val mockUser = UserAccountDTO(userCode = "U1", houseCode = "H1")
        val slot = slot<CreateChoreDTO>()

        coEvery { repository.getUserProfile(any()) } returns Response.success(mockUser)
        coEvery { repository.createChore(capture(slot)) } returns Response.success("OK")

        viewModel.loadUserData("token")
        advanceUntilIdle()

        // Act
        viewModel.createChore(
            userToken = "token",
            name = "Buttare Spazzatura",
            description = "",
            dates = listOf(monday, thursday),
            assignedTo = null,
            isRecursive = true
        ) {}
        advanceUntilIdle()

        // Assert
        val generatedDays = slot.captured.days!!

        // Isola le ricorrenze per i due differenti giorni della settimana
        val mondays = generatedDays.filter { it.dayOfWeek == monday.dayOfWeek }.sorted()
        val thursdays = generatedDays.filter { it.dayOfWeek == thursday.dayOfWeek }.sorted()

        assertTrue("La lista deve contenere i Lunedì", mondays.isNotEmpty())
        assertTrue("La lista deve contenere i Giovedì", thursdays.isNotEmpty())
        assertEquals("Tutti i giorni generati devono essere o Lunedì o Giovedì",
            mondays.size + thursdays.size, generatedDays.size)

        // Verifica l'esatta spaziatura settimanale nella catena dei Lunedì
        for (i in 0 until mondays.size - 1) {
            assertEquals("La sequenza dei Lunedì è interrotta o sfasata", mondays[i].plusWeeks(1), mondays[i + 1])
        }

        // Verifica l'esatta spaziatura settimanale nella catena dei Giovedì
        for (i in 0 until thursdays.size - 1) {
            assertEquals("La sequenza dei Giovedì è interrotta o sfasata", thursdays[i].plusWeeks(1), thursdays[i + 1])
        }

        // Verifica che tutte le date rispettino il limite semestrale basato sulla data più vecchia (minOrNull)
        val limitDate = monday.plusMonths(6)
        assertTrue("Nessuna data deve sforare la timeline di 6 mesi", generatedDays.all { it.isBefore(limitDate) })
    }
}