package cohappy.frontend.viewmodel

import cohappy.frontend.client.dto.enum.HouseStateEnum
import cohappy.frontend.client.dto.request.CreateHouseAdvertisementDTO
import cohappy.frontend.repository.CreateAdRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockkConstructor
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
class CreateAdViewModelTest {

    private lateinit var viewModel: CreateAdViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkConstructor(CreateAdRepository::class)
        viewModel = CreateAdViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `publishAdvertisement fails locally if inputs are invalid`() = runTest {
        viewModel.updatePrice("invalid_price")
        viewModel.updateDescription("  ")

        viewModel.publishAdvertisement("HOUSE_1", "TOKEN_1")

        assertEquals("Inserisci un prezzo valido e una descrizione", viewModel.errorMessage)
        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.isSuccess)

        coVerify(exactly = 0) { anyConstructed<CreateAdRepository>().createAdvertisement(any()) }
    }

    @Test
    fun `publishAdvertisement happy path updates state to success`() = runTest {
        val houseCode = "HOUSE_1"
        val userToken = "TOKEN_1"

        viewModel.updatePrice("450.50")
        viewModel.updateDescription("Bella stanza")
        viewModel.addImage(byteArrayOf(1, 2, 3))

        val expectedDto = CreateHouseAdvertisementDTO(
            houseCode = houseCode,
            state = HouseStateEnum.PUBLIC,
            publishedBy = userToken,
            description = "Bella stanza",
            images = listOf(byteArrayOf(1, 2, 3))
        )

        coEvery { anyConstructed<CreateAdRepository>().createAdvertisement(any()) } returns Response.success("OK")

        viewModel.publishAdvertisement(houseCode, userToken)

        assertTrue(viewModel.isLoading)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertTrue(viewModel.isSuccess)
        assertEquals(null, viewModel.errorMessage)

        coVerify(exactly = 1) { anyConstructed<CreateAdRepository>().createAdvertisement(expectedDto) }
    }

    @Test
    fun `publishAdvertisement unhappy path updates error message`() = runTest {
        viewModel.updatePrice("500")
        viewModel.updateDescription("Stanza doppia")

        coEvery { anyConstructed<CreateAdRepository>().createAdvertisement(any()) } returns Response.error(400, "".toResponseBody())

        viewModel.publishAdvertisement("HOUSE_1", "TOKEN_1")
        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.isSuccess)
        assertEquals("Errore del server: 400", viewModel.errorMessage)
    }

    @Test
    fun `publishAdvertisement network failure catches exception`() = runTest {
        viewModel.updatePrice("500")
        viewModel.updateDescription("Stanza doppia")

        coEvery { anyConstructed<CreateAdRepository>().createAdvertisement(any()) } throws Exception("No Internet")

        viewModel.publishAdvertisement("HOUSE_1", "TOKEN_1")
        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.isSuccess)
        assertEquals("Nessuna connessione", viewModel.errorMessage)
    }

    @Test
    fun `state resets work correctly`() {
        viewModel.updatePrice("500")
        viewModel.updateDescription("Stanza")

        // Simulo l'impostazione degli stati
        val errorField = viewModel.javaClass.getDeclaredField("errorMessage")
        errorField.isAccessible = true
        errorField.set(viewModel, "Errore precedente")

        val successField = viewModel.javaClass.getDeclaredField("isSuccess")
        successField.isAccessible = true
        successField.set(viewModel, true)

        viewModel.resetError()
        viewModel.resetSuccess()

        assertEquals(null, viewModel.errorMessage)
        assertFalse(viewModel.isSuccess)
    }
}