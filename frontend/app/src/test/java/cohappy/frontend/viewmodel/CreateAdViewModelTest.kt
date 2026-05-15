package cohappy.frontend.viewmodel

import android.util.Log
import cohappy.frontend.client.dto.request.CreateHouseAdvertisementDTO
import cohappy.frontend.repository.CreateAdRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class CreateAdViewModelTest {

    private lateinit var viewModel: CreateAdViewModel
    private val repository: CreateAdRepository = mockk()
    
    // UnconfinedTestDispatcher esegue le coroutine immediatamente, 
    // rendendo isLoading=true/false deterministico nei test.
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Mock obbligatorio per Log, altrimenti crasha nei test unitari
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        
        viewModel = CreateAdViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `publishOrUpdateAdvertisement fails locally if inputs are invalid`() = runTest {
        viewModel.updatePrice("invalid_price")
        viewModel.updateDescription("  ")

        viewModel.publishOrUpdateAdvertisement("HOUSE_1", "TOKEN_1")

        assertEquals("Inserisci un prezzo valido e una descrizione", viewModel.errorMessage)
        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.isSuccess)

        coVerify(exactly = 0) { repository.createAdvertisement(any()) }
    }

    @Test
    fun `publishOrUpdateAdvertisement happy path updates state to success`() = runTest {
        val houseCode = "HOUSE_1"
        val userToken = "TOKEN_1"

        viewModel.updatePrice("450.50")
        viewModel.updateDescription("Bella stanza")
        
        coEvery { repository.createAdvertisement(any()) } returns Response.success("OK")

        viewModel.publishOrUpdateAdvertisement(houseCode, userToken)

        // Con UnconfinedTestDispatcher, non serve advanceUntilIdle() 
        // per le operazioni semplici, lo stato è già aggiornato.
        assertFalse(viewModel.isLoading)
        assertTrue(viewModel.isSuccess)
        assertEquals(null, viewModel.errorMessage)

        coVerify(exactly = 1) { 
            repository.createAdvertisement(match { 
                it.houseCode == houseCode && it.description == "Bella stanza"
            }) 
        }
    }

    @Test
    fun `publishOrUpdateAdvertisement unhappy path updates error message`() = runTest {
        viewModel.updatePrice("500")
        viewModel.updateDescription("Stanza doppia")

        coEvery { repository.createAdvertisement(any()) } returns Response.error(400, "".toResponseBody())

        viewModel.publishOrUpdateAdvertisement("HOUSE_1", "TOKEN_1")

        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.isSuccess)
        assertEquals("Errore del server: 400", viewModel.errorMessage)
    }

    @Test
    fun `publishOrUpdateAdvertisement network failure catches exception`() = runTest {
        viewModel.updatePrice("500")
        viewModel.updateDescription("Stanza doppia")

        coEvery { repository.createAdvertisement(any()) } throws Exception("No Internet")

        viewModel.publishOrUpdateAdvertisement("HOUSE_1", "TOKEN_1")

        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.isSuccess)
        assertEquals("Nessuna connessione", viewModel.errorMessage)
    }

    @Test
    fun `state resets work correctly`() = runTest {
        // Prepariamo uno stato di errore
        viewModel.updatePrice("invalid")
        viewModel.publishOrUpdateAdvertisement("H1", "T1")
        assertEquals("Inserisci un prezzo valido e una descrizione", viewModel.errorMessage)

        // Reset
        viewModel.resetError()
        viewModel.resetSuccess()

        assertEquals(null, viewModel.errorMessage)
        assertFalse(viewModel.isSuccess)
    }
}
