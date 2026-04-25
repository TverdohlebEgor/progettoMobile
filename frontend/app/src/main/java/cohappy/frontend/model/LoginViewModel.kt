package cohappy.frontend.model

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cohappy.frontend.repository.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val showError: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val token: String? = null
)

class LoginViewModel(
    private val authRepository: LoginRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        _uiState.value = LoginUiState(isLoading = true, showError = false)
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            result.onSuccess { token ->
                _uiState.value = LoginUiState(isLoginSuccessful = true, token = token)
            }.onFailure {
                _uiState.value = LoginUiState(showError = true)
            }
        }
    }
}

class LoginViewModelFactory(private val sharedPref: SharedPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            val repo = LoginRepository(sharedPref)
            return LoginViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}