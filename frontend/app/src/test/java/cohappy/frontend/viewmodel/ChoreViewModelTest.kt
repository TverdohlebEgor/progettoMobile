package cohappy.frontend.viewmodel

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.UserApiClient
import cohappy.frontend.client.dto.request.PatchChoreDTO
import cohappy.frontend.client.dto.response.GetChoreDTO
import cohappy.frontend.client.dto.response.UserAccountDTO
import cohappy.frontend.repository.ChoreRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
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
class ChoreViewModelTest {

    private lateinit var viewModel: ChoreViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val mockUserApi = mockk<UserApiClient>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockkObject(ClientSingleton)
        every { ClientSingleton.userApi } returns mockUserApi

        mockkConstructor(ChoreRepository::class)

        viewModel = ChoreViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `loadUserData happy path loads profile and chores`() = runTest {
        val userToken = "token123"
        val houseCode = "HOUSE_1"

        val mockUser = UserAccountDTO(name = "Ale", houseCode = houseCode)
        val mockChores = listOf(GetChoreDTO(choreCode = "C1", name = "Bagno", completed = false))

        coEvery { mockUserApi.getUserProfile("token123") } returns Response.success(mockUser)
        coEvery { anyConstructed<ChoreRepository>().fetchUserChores(houseCode) } returns Response.success(mockChores)

        viewModel.loadUserData(userToken)

        assertTrue(viewModel.isLoading)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertEquals("Ale", viewModel.nomeUtente)
        assertEquals(1, viewModel.chores.size)
        assertEquals("Bagno", viewModel.chores[0].title)
    }

    @Test
    fun `loadUserData unhappy path user api fails sets offline`() = runTest {
        val userToken = "token123"

        coEvery { mockUserApi.getUserProfile("token123") } returns Response.error(500, "".toResponseBody())

        viewModel.loadUserData(userToken)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertEquals("Errore API", viewModel.nomeUtente)
        assertTrue(viewModel.chores.isEmpty())
    }

    @Test
    fun `loadUserData unhappy path user has no house`() = runTest {
        val userToken = "token123"
        val mockUser = UserAccountDTO(name = "Ale", houseCode = null)

        coEvery { mockUserApi.getUserProfile("token123") } returns Response.success(mockUser)

        viewModel.loadUserData(userToken)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertEquals("Ale", viewModel.nomeUtente)
        assertTrue(viewModel.chores.isEmpty())

        coVerify(exactly = 0) { anyConstructed<ChoreRepository>().fetchUserChores(any()) }
    }

    @Test
    fun `toggleChoreCompletion happy path updates state`() = runTest {
        // Pre-popoliamo la lista per il test
        val userToken = "token123"
        val houseCode = "HOUSE_1"
        val mockUser = UserAccountDTO(name = "Ale", houseCode = houseCode)
        val mockChores = listOf(GetChoreDTO(choreCode = "C1", name = "Bagno", completed = false))

        coEvery { mockUserApi.getUserProfile(any()) } returns Response.success(mockUser)
        coEvery { anyConstructed<ChoreRepository>().fetchUserChores(any()) } returns Response.success(mockChores)

        viewModel.loadUserData(userToken)
        advanceUntilIdle()

        // Simulo il click
        coEvery { anyConstructed<ChoreRepository>().updateChoreStatus(any()) } returns Response.success("OK")

        viewModel.toggleChoreCompletion("C1", "user1", true)
        assertTrue(viewModel.isLoading)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertTrue(viewModel.chores[0].isCompleted)
    }

    @Test
    fun `toggleChoreCompletion unhappy path leaves state unchanged`() = runTest {
        // Pre-popoliamo la lista
        val userToken = "token123"
        val houseCode = "HOUSE_1"
        val mockUser = UserAccountDTO(name = "Ale", houseCode = houseCode)
        val mockChores = listOf(GetChoreDTO(choreCode = "C1", name = "Bagno", completed = false))

        coEvery { mockUserApi.getUserProfile(any()) } returns Response.success(mockUser)
        coEvery { anyConstructed<ChoreRepository>().fetchUserChores(any()) } returns Response.success(mockChores)

        viewModel.loadUserData(userToken)
        advanceUntilIdle()

        // Simulo il click che fallisce
        coEvery { anyConstructed<ChoreRepository>().updateChoreStatus(any()) } returns Response.error(500, "".toResponseBody())

        viewModel.toggleChoreCompletion("C1", "user1", true)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.chores[0].isCompleted) // Rimane false perché la patch è fallita
    }


}