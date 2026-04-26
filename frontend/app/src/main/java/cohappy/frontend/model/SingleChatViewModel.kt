package cohappy.frontend.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cohappy.frontend.client.dto.request.AddMessageDTO
import cohappy.frontend.client.dto.request.CreateChatDTO
import cohappy.frontend.client.dto.response.ChatMessageDTO
import cohappy.frontend.repository.SingleChatRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class ChatUiState(
    val isLoading: Boolean = true,
    val nomeChat: String = "Caricamento...",
    val messaggi: List<ChatMessageDTO> = emptyList(),
    val resolvedChatCode: String = "",
    val resolvedAnnuncioId: String = "",
    val mioUserCode: String = ""
)

class SingleChatViewModel(
    private val repository: SingleChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    fun initChat(chatCode: String, mioUserCode: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, mioUserCode = mioUserCode)
        pollingJob?.cancel()

        viewModelScope.launch {
            var nomeConversazione = "Sconosciuto"
            var idChatDaUsare = ""
            var otherUserCodeForSearch = chatCode

            try {
                val profileResp = repository.getUserProfile(chatCode)
                if (profileResp.isSuccessful && profileResp.body() != null) {
                    val user = profileResp.body()!!
                    val fullName = "${user.name ?: ""} ${user.surname ?: ""}".trim()
                    if (fullName.isNotBlank()) nomeConversazione = fullName
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("TAG_CHAT", "Errore API profilo: ${e.message}")
            }

            try {
                val chatsResp = repository.getUserChats(mioUserCode)
                val mieChats = chatsResp.body() ?: emptyList()
                val chatTrovata = mieChats.find {
                    it.chatCode == chatCode ||
                            (it.participating != null && it.participating!!.contains(mioUserCode) && it.participating!!.contains(
                                chatCode
                            ) && it.participating!!.size == 2)
                }

                if (chatTrovata != null) {
                    idChatDaUsare = chatTrovata.chatCode ?: ""
                    if (nomeConversazione == "Sconosciuto" && !chatTrovata.name.isNullOrBlank()) {
                        nomeConversazione  = chatTrovata.name!!
                    }
                    chatTrovata.participating?.find { it != mioUserCode }
                        ?.let { otherUserCodeForSearch = it }
                } else {
                    val createDto = CreateChatDTO(
                        participating = listOf(mioUserCode, chatCode),
                        name = if (nomeConversazione  == "Sconosciuto") "Nuova Chat" else nomeConversazione,
                        immage = null
                    )
                    val createResp = repository.createChat(createDto)
                    if (createResp.isSuccessful && createResp.body() != null) {
                        idChatDaUsare = createResp.body()!!.replace("\"", "").trim()
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("TAG_CHAT", "Errore API ricerca/creazione: ${e.message}")
            }

            var annuncioTrovato = ""
            try {
                val adsResp = repository.getHouseAdvertisements()
                if (adsResp.isSuccessful && adsResp.body() != null) {
                    adsResp.body()!!.find { it.publishedByCode == otherUserCodeForSearch }?.let {
                        annuncioTrovato = it.houseCode ?: ""
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("TAG_CHAT", "Errore ricerca annuncio: ${e.message}")
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                nomeChat = nomeConversazione,
                resolvedChatCode = idChatDaUsare,
                resolvedAnnuncioId = annuncioTrovato
            )

            if (idChatDaUsare.isNotBlank()) {
                startPolling(idChatDaUsare)
            }
        }
    }

    private fun startPolling(chatId: String) {
        pollingJob = viewModelScope.launch {
            while (isActive) {
                try {
                    val response = repository.getMessages(chatId)
                    if (response.isSuccessful && response.body() != null) {
                        val newMessages = response.body()!!
                        if (newMessages != _uiState.value.messaggi) {
                            _uiState.value = _uiState.value.copy(messaggi = newMessages)
                        }
                    }
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    Log.e("TAG_CHAT", "Errore polling: ${e.message}")
                }
                delay(1000)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }

    fun stopPolling() {
        pollingJob?.cancel()
    }

    fun sendMessage(testo: String, immage: ByteArray? = null) {
        val currentState = _uiState.value
        if (testo.isBlank() || currentState.resolvedChatCode.isBlank()) return

        val newMessage = ChatMessageDTO(
            message = testo,
            userCode = currentState.mioUserCode,
            messageImmage = immage,
            timestamp = LocalDateTime.now(),
        )
        _uiState.value = currentState.copy(messaggi = currentState.messaggi + newMessage)

        viewModelScope.launch {
            val dto = AddMessageDTO(
                message = testo,
                userCode = currentState.mioUserCode,
                chatCode = currentState.resolvedChatCode
            )
            repository.sendMessage(dto).onFailure { Log.e("TAG_CHAT", "Errore invio") }
        }
    }
}

class SingleChatViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SingleChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SingleChatViewModel(SingleChatRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}