package cohappy.frontend.viewmodel

import android.util.Log
import cohappy.frontend.client.dto.response.GetHouseDTO
import cohappy.frontend.client.dto.response.UserAccountDTO
import cohappy.frontend.repository.RoommateProfileRepository
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
class HouseProfileViewModelTest {

    private lateinit var viewModel: RommateProfileViewModel
    private val repository: RoommateProfileRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        viewModel = RommateProfileViewModel()
        // Iniezione manuale se il costruttore non lo permette, altrimenti usa anyConstructed
        val field = viewModel.javaClass.getDeclaredField("repository")
        field.isAccessible = true
        field.set(viewModel, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
        unmockkAll()
    }

    @Test
    fun `loadProfile happy path updates state`() = runTest {
        val mockUser = UserAccountDTO(name = "Ale", surname = "Boss", images = listOf(byteArrayOf(1)))
        coEvery { repository.fetchUserProfile("token") } returns Response.success(mockUser)

        viewModel.loadProfile("token")
        assertTrue(viewModel.isLoading)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertEquals("Ale", viewModel.userName)
        assertEquals("Boss", viewModel.userSurname)
        assertTrue(viewModel.profileImageBytes != null)
    }

    @Test
    fun `loadHouseDetails happy path detects admin status`() = runTest {
        val mockHouse = GetHouseDTO(street = "Via Roma", civicNumber = 1, admins = listOf("my_id"))
        // Prepariamo l'ID utente nel VM (solitamente settato da loadProfile)
        val myUserField = viewModel.javaClass.getDeclaredField("myUserCode")
        myUserField.isAccessible = true
        myUserField.set(viewModel, "my_id")

        coEvery { cohappy.frontend.client.ClientSingleton.houseApi.getHouse("H1") } returns Response.success(mockHouse)

        viewModel.loadHouseDetails("H1", "my_id")
        advanceUntilIdle()

        assertEquals("Via Roma 1", viewModel.houseAddress)
        assertTrue(viewModel.isCurrentUserAdmin)
    }

    @Test
    fun `updateHouseCode unhappy path 409 sets error message`() = runTest {
        coEvery { cohappy.frontend.client.ClientSingleton.houseApi.modifyHouse(any()) } returns Response.error(409, "Exists".toResponseBody())

        viewModel.updateHouseCode("OLD", "EXISTING")
        assertTrue(viewModel.isUpdatingCode)
        advanceUntilIdle()

        assertFalse(viewModel.isUpdatingCode)
        assertEquals("Codice già esistente!", viewModel.codeUpdateError)
    }

    @Test
    fun `leaveHouse success sets leftHouse flag`() = runTest {
        coEvery { cohappy.frontend.client.ClientSingleton.houseApi.removeUser(any()) } returns Response.success("OK")

        viewModel.leaveHouse("token", "H1")
        advanceUntilIdle()

        assertTrue(viewModel.hasLeftHouse)
    }

    @Test
    fun `uploadNewImage network failure logs error but keeps local bitmap`() = runTest {
        val img = byteArrayOf(1, 2, 3)
        coEvery { repository.updateUserImage(any(), any()) } throws Exception("Network Down")

        viewModel.uploadNewImage("token", img)
        advanceUntilIdle()

        // L'immagine locale deve restare per feedback immediato
        assertTrue(viewModel.profileImageBytes?.contentEquals(img) == true)
        verify { Log.e(any(), any(), any()) }
    }
}