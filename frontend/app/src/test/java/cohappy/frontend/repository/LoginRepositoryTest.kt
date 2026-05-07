package cohappy.frontend.repository

import android.content.SharedPreferences
import android.content.res.Resources
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.UserApiClient
import cohappy.frontend.expections.ErrorMessages.SERVER_ERROR
import cohappy.frontend.expections.ErrorMessages.WRONG_CREDENTIAL_LOGIN
import cohappy.frontend.expections.NotFoundException
import cohappy.frontend.expections.ServerErrorException
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class LoginRepositoryTest {

    private val sharedPref = mockk<SharedPreferences>(relaxed = true)
    private val editor = mockk<SharedPreferences.Editor>(relaxed = true)
    private val userApi = mockk<UserApiClient>()
    private lateinit var repository: LoginRepository

    @Before
    fun setup() {
        mockkObject(ClientSingleton)
        every { ClientSingleton.userApi } returns userApi
        every { sharedPref.edit() } returns editor
        repository = LoginRepository(sharedPref)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `login success`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val userCode = "\"USR_123\""
        val expectedUserCode = "USR_123"
        
        coEvery { userApi.login(any()) } returns Response.success(userCode)

        val result = repository.login(email, password)

        assertTrue(result.isSuccess)
        assertEquals(expectedUserCode, result.getOrNull())
        verify { editor.putString("USER_TOKEN", expectedUserCode) }
    }

    @Test
    fun `login failure 400 returns ServerErrorException`() = runTest {
        val email = "test@example.com"
        val password = "wrong_password"
        
        coEvery { userApi.login(any()) } returns Response.error(400, "".toResponseBody())
        val result = repository.login(email, password)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ServerErrorException)
        assertEquals(SERVER_ERROR, result.exceptionOrNull()?.message)
    }

    @Test
    fun `login failure 404 returns Exception`() = runTest {
        val email = "nonexistent@example.com"
        val password = "password123"
        coEvery { userApi.login(any()) } returns Response.error(404, "".toResponseBody())
        val result = repository.login(email, password)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotFoundException)
        assertEquals(WRONG_CREDENTIAL_LOGIN, result.exceptionOrNull()?.message)
    }

    @Test
    fun `login failure 500 returns ServerErrorException`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        coEvery { userApi.login(any()) } returns Response.error(500, "".toResponseBody())
        val result = repository.login(email, password)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ServerErrorException)
        assertEquals(SERVER_ERROR , result.exceptionOrNull()?.message)
    }

    @Test
    fun `login exception returns failure`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        val exception = Exception("Network timeout")
        coEvery { userApi.login(any()) } throws exception
        val result = repository.login(email, password)
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
