package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.HouseApiClient
import cohappy.frontend.client.UserApiClient
import cohappy.frontend.client.dto.response.UserAccountDTO

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class HouseProfileRepositoryTest {

    private val houseApi = mockk<HouseApiClient>()
    private val userApi = mockk<UserApiClient>()
    private lateinit var repository: RoommateProfileRepository

    @Before
    fun setup() {
        mockkObject(ClientSingleton)
        every { ClientSingleton.houseApi } returns houseApi
        every { ClientSingleton.userApi } returns userApi
        repository = RoommateProfileRepository()
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `fetchUserProfile success returns data`() = runTest {
        val dto = UserAccountDTO(name = "Ale")
        coEvery { userApi.getUserProfile("U1") } returns Response.success(dto)
        val result = repository.fetchUserProfile("U1")
        assertTrue(result.isSuccessful)
        assertEquals("Ale", result.body()?.name)
    }

    @Test
    fun `fetchUserProfile network exception throws`() = runTest {
        coEvery { userApi.getUserProfile(any()) } throws IOException("No internet")
        try {
            repository.fetchUserProfile("U1")
        } catch (e: Exception) {
            assertTrue(e is IOException)
        }
    }

    @Test
    fun `updateUserImage happy path returns success`() = runTest {
        coEvery { userApi.patchUser(any()) } returns Response.success("OK")
        val result = repository.updateUserImage("U1", byteArrayOf(0))
        assertTrue(result.isSuccessful)
    }

    @Test
    fun `updateUserImage unhappy path 400`() = runTest {
        coEvery { userApi.patchUser(any()) } returns Response.error(400, "".toResponseBody())
        val result = repository.updateUserImage("U1", byteArrayOf(0))
        assertFalse(result.isSuccessful)
        assertEquals(400, result.code())
    }
}