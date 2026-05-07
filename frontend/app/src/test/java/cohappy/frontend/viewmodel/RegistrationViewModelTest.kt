package cohappy.frontend.viewmodel

import cohappy.frontend.repository.RegistrationRepository
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
class RegistrationViewModelTest {

    private lateinit var viewModel: RegistrationViewModel
    private val repository: RegistrationRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RegistrationViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `register success updates uiState to success`() = runTest {
        val name = "Mario"
        val surname = "Rossi"
        val birthDate = "1990-01-01"
        val email = "mario@example.com"
        val phone = "1234567890"
        val password = "password123"

        coEvery { repository.registerUser(any()) } returns Result.success("USER_CODE")

        viewModel.register(name, surname, birthDate, email, phone, password)
        
        assertEquals(true, viewModel.uiState.value.isLoading)
        
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(false, state.showError)
        assertEquals(true, state.isRegistrationSuccessful)
    }

    @Test
    fun `register repository failure updates uiState to error`() = runTest {
        val name = "Mario"
        val surname = "Rossi"
        val birthDate = "1990-01-01"
        val email = "mario@example.com"
        val phone = "1234567890"
        val password = "password123"
        val errorMessage = "Registration failed"

        coEvery { repository.registerUser(any()) } returns Result.failure(Exception(errorMessage))

        viewModel.register(name, surname, birthDate, email, phone, password)
        
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(true, state.showError)
        assertEquals(errorMessage, state.errorMessage)
        assertEquals(false, state.isRegistrationSuccessful)
    }

    @Test
    fun `register validation failure name invalid`() = runTest {
        viewModel.register("M", "Rossi", "1990-01-01", "m@e.c", "1234567890", "password123")
        assertEquals(true, viewModel.uiState.value.nameError)

        viewModel.register("Mario1", "Rossi", "1990-01-01", "m@e.c", "1234567890", "password123")
        assertEquals(true, viewModel.uiState.value.nameError)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `register validation failure surname invalid`() = runTest {
        viewModel.register("Mario", "", "1990-01-01", "m@e.c", "1234567890", "password123")
        assertEquals(true, viewModel.uiState.value.surnameError)
    }

    @Test
    fun `register validation failure birthDate blank`() = runTest {
        viewModel.register("Mario", "Rossi", " ", "m@e.c", "1234567890", "password123")
        assertEquals(true, viewModel.uiState.value.dateError)
    }

    @Test
    fun `register validation failure email invalid`() = runTest {
        viewModel.register("Mario", "Rossi", "1990-01-01", "invalid-email", "1234567890", "password123")
        assertEquals(true, viewModel.uiState.value.emailError)
    }

    @Test
    fun `register validation failure phone invalid`() = runTest {
        viewModel.register("Mario", "Rossi", "1990-01-01", "m@e.c", "123456789", "password123")
        assertEquals(true, viewModel.uiState.value.phoneError)

        viewModel.register("Mario", "Rossi", "1990-01-01", "m@e.c", "123456789a", "password123")
        assertEquals(true, viewModel.uiState.value.phoneError)
    }

    @Test
    fun `register validation failure password too short`() = runTest {
        viewModel.register("Mario", "Rossi", "1990-01-01", "m@e.c", "1234567890", "12345")
        assertEquals(true, viewModel.uiState.value.passwordError)
    }
}
