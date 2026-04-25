package cohappy.frontend.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cohappy.frontend.client.dto.request.RegisterDTO
import cohappy.frontend.repository.RegistrationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegistrationUiState(
    val isLoading: Boolean = false,
    val backendError: Boolean = false,
    val isRegistrationSuccessful: Boolean = false,

    val nameError: Boolean = false,
    val surnameError: Boolean = false,
    val dateError: Boolean = false,
    val emailError: Boolean = false,
    val phoneError: Boolean = false,
    val passwordError: Boolean = false
)

class RegistrationViewModel(
    private val repository: RegistrationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    fun register(nome: String, cognome: String, dataNascita: String, email: String, telefono: String, password: String) {
        val isNameValid = nome.isNotBlank() && nome.trim().length >= 2 && nome.trim().all { it.isLetter() }
        val isSurnameValid = cognome.isNotBlank() && cognome.trim().length >= 2 && cognome.trim().all { it.isLetter() }
        val isDateValid = dataNascita.trim().isNotBlank()
        val isEmailValid = email.isNotBlank() && email.contains("@") && email.contains(".")
        val isPhoneValid = telefono.isNotBlank() && telefono.trim().length == 10 && telefono.trim().all { it.isDigit() }
        val isPasswordValid = password.isNotBlank() && password.trim().length >= 6

        if (!isNameValid || !isSurnameValid || !isDateValid || !isEmailValid || !isPhoneValid || !isPasswordValid) {
            _uiState.value = RegistrationUiState(
                nameError = !isNameValid,
                surnameError = !isSurnameValid,
                dateError = !isDateValid,
                emailError = !isEmailValid,
                phoneError = !isPhoneValid,
                passwordError = !isPasswordValid
            )
            return
        }

        _uiState.value = RegistrationUiState(isLoading = true)

        viewModelScope.launch {
            val dto = RegisterDTO(
                name = nome, surname = cognome, birthDate = dataNascita,
                email = email, phoneNumber = telefono, password = password
            )

            val result = repository.registerUser(dto)

            result.onSuccess {
                _uiState.value = RegistrationUiState(isRegistrationSuccessful = true)
            }.onFailure { exception ->
                println("Registrazione fallita: ${exception.message}")
                _uiState.value = RegistrationUiState(backendError = true)
            }
        }
    }
}

class RegistrationViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegistrationViewModel(RegistrationRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}