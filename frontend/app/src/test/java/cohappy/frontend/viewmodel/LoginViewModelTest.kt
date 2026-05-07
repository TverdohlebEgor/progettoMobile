package cohappy.frontend.viewmodel

import cohappy.frontend.repository.LoginRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private val repository: LoginRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun login_success_updates_uiState_to_success() = runTest {
        val email = "test@example.com"
        val password = "password"
        val userCode = "USER123"
        coEvery { repository.login(email, password) } returns Result.success(userCode)

        viewModel.login(email, password)
        
        assertEquals(true, viewModel.uiState.value.isLoading)
        
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(false, state.showError)
        assertEquals(true, state.isLoginSuccessful)
        assertEquals(userCode, state.userCode)
    }

    @Test
    fun login_failure_updates_uiState_to_error() = runTest {
        val email = "test@example.com"
        val password = "wrong"
        coEvery { repository.login(email, password) } returns Result.failure(Exception("Login failed"))

        viewModel.login(email, password)
        
        assertEquals(true, viewModel.uiState.value.isLoading)

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(true, state.showError)
        assertEquals("Login failed", state.errorMessage)
        assertEquals(false, state.isLoginSuccessful)
        assertEquals(null, state.userCode)
    }
}
