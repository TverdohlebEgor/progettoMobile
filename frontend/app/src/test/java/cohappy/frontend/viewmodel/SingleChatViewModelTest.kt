package cohappy.frontend.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import cohappy.frontend.client.dto.response.ChatMessageDTO
import cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO
import cohappy.frontend.client.dto.response.UserAccountDTO
import cohappy.frontend.client.dto.response.UserChatDTO
import cohappy.frontend.repository.AdListRepository
import cohappy.frontend.repository.ChatListRepository
import cohappy.frontend.repository.SingleChatRepository
import cohappy.frontend.repository.UserProfileRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class SingleChatViewModelTest {

    private val singleChatRepository = mockk<SingleChatRepository>()
    private val userRepository= mockk<UserProfileRepository>()
    private val houseAdvRepository= mockk<AdListRepository>()
    private val chatListRepository = mockk<ChatListRepository>()
    private lateinit var viewModel: SingleChatViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.e(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0

        mockkStatic(BitmapFactory::class)
        val mockBitmap = mockk<Bitmap>(relaxed = true)
        every { BitmapFactory.decodeByteArray(any(), any(), any()) } returns mockBitmap
        every { mockBitmap.compress(any(), any(), any()) } returns true

        coEvery { userRepository.fetchUserProfile(any()) } returns Result.failure(Exception())
        coEvery { chatListRepository.getUserChats(any()) } returns Result.success(emptyList())
        coEvery { houseAdvRepository.fetchAds() } returns Result.success(emptyList())
        coEvery { singleChatRepository.getMessages(any()) } returns Result.success(emptyList())
        coEvery { chatListRepository.createChat(any()) } returns Result.success("id")
        
        viewModel = SingleChatViewModel(
            singleChatRepository,
            userRepository,
            houseAdvRepository,
            chatListRepository
        )
    }

    @After
    fun tearDown() {
        if (::viewModel.isInitialized) {
            viewModel.stopPolling()
        }
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `initChat with existing chat should update state correctly`() = runTest(testDispatcher) {
        val chatCode = "OTHER_USER"
        val mioUserCode = "ME"
        val chatId = "CHAT_123"
        val userProfile = UserAccountDTO(name = "Mario", surname = "Rossi")
        coEvery { userRepository.fetchUserProfile(chatCode) } returns Result.success(userProfile)
        val userChat = UserChatDTO(chatCode = chatId, participating = listOf(mioUserCode, chatCode), name = "Mario Rossi")
        coEvery { chatListRepository.getUserChats(mioUserCode) } returns Result.success(listOf(userChat))
        val ads = listOf(GetHouseAdvertesimentDTO(houseCode = "HOUSE_1", publishedByCode = chatCode))
        coEvery { houseAdvRepository.fetchAds() } returns Result.success(ads)
        val messages = listOf(ChatMessageDTO(message = "Hello", userCode = chatCode))
        coEvery { singleChatRepository.getMessages(chatId) } returns Result.success(messages)
        
        viewModel.initChat(chatCode, mioUserCode)
        
        advanceTimeBy(100)
        runCurrent()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Mario Rossi", state.nomeChat)
        assertEquals(chatId, state.resolvedChatCode)
        assertEquals("HOUSE_1", state.resolvedAnnuncioId)
        
        assertEquals(1, viewModel.uiState.value.messaggi.size)
        assertEquals("Hello", viewModel.uiState.value.messaggi[0].message)
        
        viewModel.stopPolling()
    }

    @Test
    fun `initChat with new chat should create it and update state`() = runTest(testDispatcher) {
        val chatCode = "NEW_USER"
        val mioUserCode = "ME"
        val newChatId = "NEW_CHAT_ID"
        coEvery { userRepository.fetchUserProfile(chatCode) } returns Result.success(UserAccountDTO(name = "New", surname = "User"))
        coEvery { chatListRepository.getUserChats(mioUserCode) } returns Result.success(emptyList())
        coEvery { chatListRepository.createChat(any()) } returns Result.success(newChatId)
        coEvery { houseAdvRepository.fetchAds() } returns Result.success(emptyList())
        
        viewModel.initChat(chatCode, mioUserCode)
        advanceTimeBy(500)
        runCurrent()
        
        val state = viewModel.uiState.value
        assertEquals("New User", state.nomeChat)
        assertEquals(newChatId, state.resolvedChatCode)
        coVerify { chatListRepository.createChat(match { it.participating.containsAll(listOf(mioUserCode, chatCode)) }) }
        
        viewModel.stopPolling()
    }

    @Test
    fun `sendMessage with text should update local state and call repository`() = runTest(testDispatcher) {
        val chatCode = "OTHER"
        val mioUserCode = "ME"
        val chatId = "CHAT_ID"
        coEvery { userRepository.fetchUserProfile(any()) } returns Result.failure(Exception())
        coEvery { chatListRepository.getUserChats(any()) } returns Result.success(listOf(UserChatDTO(chatCode = chatId, participating = listOf(mioUserCode, chatCode))))
        coEvery { houseAdvRepository.fetchAds() } returns Result.success(emptyList())
        coEvery { singleChatRepository.getMessages(any()) } returns Result.success(emptyList())
        
        viewModel.initChat(chatCode, mioUserCode)
        advanceTimeBy(500)
        runCurrent()
        
        val messageText = "Test message"
        coEvery { singleChatRepository.sendMessage(any()) } returns Result.success(Unit)
        
        viewModel.sendMessage(messageText)
        assertTrue(viewModel.uiState.value.isSending)
        assertEquals(1, viewModel.uiState.value.messaggi.size)
        assertEquals(messageText, viewModel.uiState.value.messaggi[0].message)
        
        advanceTimeBy(600)
        runCurrent()
        
        assertFalse(viewModel.uiState.value.isSending)
        coVerify {
            singleChatRepository.sendMessage(match {
                it.message == messageText && it.chatCode == chatId && it.userCode == mioUserCode 
            }) 
        }
        
        viewModel.stopPolling()
    }

    @Test
    fun `sendMessage with image should update state and call repository`() = runTest(testDispatcher) {
        val chatCode = "OTHER"
        val mioUserCode = "ME"
        val chatId = "CHAT_ID"
        coEvery { chatListRepository.getUserChats(any()) } returns Result.success(listOf(UserChatDTO(chatCode = chatId, participating = listOf(mioUserCode, chatCode))))
        viewModel.initChat(chatCode, mioUserCode)
        advanceTimeBy(500)
        runCurrent()

        val imageBytes = byteArrayOf(1, 2, 3)
        coEvery { singleChatRepository.sendMessage(any()) } returns Result.success(Unit)

        viewModel.sendMessage("", imageBytes)

        assertTrue(viewModel.uiState.value.isSending)
        assertEquals(1, viewModel.uiState.value.messaggi.size)
        assertTrue(viewModel.uiState.value.messaggi[0].messageImage != null)

        advanceTimeBy(600)
        runCurrent()

        assertFalse(viewModel.uiState.value.isSending)
        coVerify {
            singleChatRepository.sendMessage(match {
                it.message == "" && it.chatCode == chatId && it.messageImage != null
            })
        }
        viewModel.stopPolling()
    }

    @Test
    fun `sendMessage with empty content should not do anything`() = runTest(testDispatcher) {
        viewModel.sendMessage("", null)
        coVerify(exactly = 0) { singleChatRepository.sendMessage(any()) }
    }

    @Test
    fun `chat should poll messages periodically`() = runTest(testDispatcher) {
        val chatCode = "OTHER"
        val mioUserCode = "ME"
        val chatId = "CHAT_ID"
        coEvery { userRepository.fetchUserProfile(any()) } returns Result.failure(Exception())
        coEvery { chatListRepository.getUserChats(any()) } returns Result.success(listOf(UserChatDTO(chatCode = chatId, participating = listOf(mioUserCode, chatCode))))
        coEvery { houseAdvRepository.fetchAds() } returns Result.success(emptyList())
        val initialMessages = emptyList<ChatMessageDTO>()
        val newMessages = listOf(ChatMessageDTO(message = "New message", userCode = chatCode))
        
        coEvery { singleChatRepository.getMessages(chatId) } returnsMany listOf(
            Result.success(initialMessages),
            Result.success(newMessages)
        )

        viewModel.initChat(chatCode, mioUserCode)
        
        advanceTimeBy(100)
        runCurrent()
        assertEquals(0, viewModel.uiState.value.messaggi.size)
        
        advanceTimeBy(1000)
        runCurrent()

        assertEquals(1, viewModel.uiState.value.messaggi.size)
        assertEquals("New message", viewModel.uiState.value.messaggi[0].message)
        coVerify(atLeast = 2) { singleChatRepository.getMessages(chatId) }
        
        viewModel.stopPolling()
    }

    @Test
    fun `polling failure should log error and not crash`() = runTest(testDispatcher) {
        val chatCode = "OTHER"
        val mioUserCode = "ME"
        val chatId = "CHAT_1"
        coEvery { chatListRepository.getUserChats(mioUserCode) } returns Result.success(listOf(UserChatDTO(chatCode = chatId, participating = listOf(mioUserCode, chatCode))))
        coEvery { singleChatRepository.getMessages(chatId) } returns Result.failure(Exception("Server down"))

        viewModel.initChat(chatCode, mioUserCode)
        advanceTimeBy(100)
        runCurrent()
        
        coVerify { singleChatRepository.getMessages(chatId) }
        
        advanceTimeBy(1000)
        runCurrent()
        
        coVerify(atLeast = 2) { singleChatRepository.getMessages(chatId) }
        viewModel.stopPolling()
    }
}
