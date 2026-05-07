package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.UserApiClient
import cohappy.frontend.client.dto.request.RegisterDTO
import cohappy.frontend.expections.ErrorMessages.ALREADY_USED_CREDENTIAL_REGISTRATION
import cohappy.frontend.expections.ErrorMessages.SERVER_ERROR
import cohappy.frontend.expections.ServerErrorException
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class RegisterRepositoryTest {

    private val userApi = mockk<UserApiClient>()
    private val repository = RegistrationRepository()

    @Before
    fun setup() {
        mockkObject(ClientSingleton)
        every { ClientSingleton.userApi } returns userApi
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `register success cleans token and returns success`() = runTest {
        val dto = mockk<RegisterDTO>()
        val rawUserCode = "\"USR_REG_123\""
        val expectedUserCode = "USR_REG_123"

        coEvery { userApi.register(dto) } returns Response.success(rawUserCode)

        val result = repository.registerUser(dto)

        assertTrue(result.isSuccess)
        assertEquals(expectedUserCode, result.getOrNull())
    }

    @Test
    fun `register failure 400 returns specific error message`() = runTest {
        val dto = mockk<RegisterDTO>()

        coEvery { userApi.register(dto) } returns Response.error(400, "".toResponseBody())

        val result = repository.registerUser(dto)

        assertTrue(result.isFailure)
        assertEquals(ALREADY_USED_CREDENTIAL_REGISTRATION, result.exceptionOrNull()?.message)
    }

    @Test
    fun `register failure 500 returns ServerErrorException`() = runTest {
        val dto = mockk<RegisterDTO>()

        coEvery { userApi.register(dto) } returns Response.error(500, "".toResponseBody())

        val result = repository.registerUser(dto)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ServerErrorException)
        assertEquals(SERVER_ERROR, result.exceptionOrNull()?.message)
    }

    @Test
    fun `register exception returns failure`() = runTest {
        val dto = mockk<RegisterDTO>()
        val exception = Exception("Network failure")

        coEvery { userApi.register(dto) } throws exception

        val result = repository.registerUser(dto)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
