package cohappy.frontend.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cohappy.frontend.client.dto.request.AddMessageDTO
import cohappy.frontend.client.dto.request.CreateChatDTO
import cohappy.frontend.client.dto.response.ChatMessageDTO
import cohappy.frontend.repository.AdListRepository
import cohappy.frontend.repository.ChatListRepository
import cohappy.frontend.repository.SingleChatRepository
import cohappy.frontend.repository.UserProfileRepository
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
    val isSending: Boolean = false,
    val nomeChat: String = "Caricamento...",
    val immagineChat: ByteArray? = null,
    val messaggi: List<ChatMessageDTO> = emptyList(),
    val resolvedChatCode: String = "",
    val resolvedAnnuncioId: String = "",
    val mioUserCode: String = ""
)

class SingleChatViewModel(
    private val singleChatRepository: SingleChatRepository,
    private val userRepository: UserProfileRepository,
    private val houseAdvRepository: AdListRepository,
    private val chatListRepository: ChatListRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    fun initChat(chatCode: String, mioUserCode: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, mioUserCode = mioUserCode)
        pollingJob?.cancel()

        viewModelScope.launch {
            var nomeConversazione = "Sconosciuto"
            var immagineConversazione: ByteArray? = null
            var idChatDaUsare = ""
            var otherUserCodeForSearch = chatCode

            try {
                val profileResult = userRepository.fetchUserProfile(chatCode)
                if (profileResult.isSuccess) {
                    val user = profileResult.getOrNull()!!
                    val fullName = "${user.name ?: ""} ${user.surname ?: ""}".trim()
                    if (fullName.isNotBlank()) nomeConversazione = fullName
                    immagineConversazione = user.images?.get(0)
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("TAG_CHAT", "Errore API profilo: ${e.message}")
            }

            try {
                val chatsResult = chatListRepository.getUserChats(mioUserCode)
                val mieChats = chatsResult.getOrNull() ?: emptyList()
                val chatTrovata = mieChats.find {
                    it.chatCode == chatCode ||
                            (it.participating != null && it.participating!!.contains(mioUserCode) && it.participating!!.contains(
                                chatCode
                            ) && it.participating!!.size == 2)
                }

                if (chatTrovata != null) {
                    idChatDaUsare = chatTrovata.chatCode ?: ""
                    if (nomeConversazione == "Sconosciuto" && !chatTrovata.name.isNullOrBlank()) {
                        nomeConversazione = chatTrovata.name!!
                    }
                    chatTrovata.participating?.find { it != mioUserCode }
                        ?.let { otherUserCodeForSearch = it }
                } else {
                    val createDto = CreateChatDTO(
                        participating = listOf(mioUserCode, chatCode),
                        name = if (nomeConversazione == "Sconosciuto") "Nuova Chat" else nomeConversazione,
                        image = null
                    )
                    val createResult = chatListRepository.createChat(createDto)
                    if (createResult.isSuccess) {
                        idChatDaUsare = createResult.getOrNull()?.replace("\"", "")?.trim() ?: ""
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("TAG_CHAT", "Errore API ricerca/creazione: ${e.message}")
            }

            var annuncioTrovato = ""
            try {
                val adsResult = houseAdvRepository.fetchAds()
                if (adsResult.isSuccess) {
                    adsResult.getOrNull()?.find { it.publishedByCode == otherUserCodeForSearch }?.let {
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
                immagineChat = immagineConversazione,
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
                    val result = singleChatRepository.getMessages(chatId)
                    if (result.isSuccess && !_uiState.value.isSending) {
                        val newMessages = result.getOrNull() ?: emptyList()
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
        val compressedImage = compressImage(immage)
        Log.d("DEBUG_CHAT", "Sending message. Image size: ${compressedImage?.size ?: 0} bytes")
        val currentState = _uiState.value
        if ((testo.isBlank() && compressedImage == null) || currentState.resolvedChatCode.isBlank()) return

        val newMessage = ChatMessageDTO(
            message = testo,
            userCode = currentState.mioUserCode,
            messageImage = compressedImage,
            timestamp = LocalDateTime.now(),
        )
        _uiState.value = currentState.copy(
            messaggi = currentState.messaggi + newMessage,
            isSending = true
        )

        viewModelScope.launch {
            val dto = AddMessageDTO(
                message = testo,
                userCode = currentState.mioUserCode,
                chatCode = currentState.resolvedChatCode,
                messageImage = compressedImage
            )
            singleChatRepository.sendMessage(dto)
                .onSuccess {
                    delay(500)
                    _uiState.value = _uiState.value.copy(isSending = false)
                }
                .onFailure {
                    Log.e("TAG_CHAT", "Errore invio")
                    _uiState.value = _uiState.value.copy(isSending = false)
                }
        }
    }
}

private fun compressImage(imageData: ByteArray?): ByteArray? {
    if(imageData == null) return null
    val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
    val outputStream = java.io.ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
    return outputStream.toByteArray()
}

class SingleChatViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SingleChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SingleChatViewModel(
                SingleChatRepository(),
                UserProfileRepository(),
                AdListRepository(),
                ChatListRepository()
                ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}