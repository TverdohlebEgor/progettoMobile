package cohappy.frontend.viewmodel

import cohappy.frontend.client.dto.response.UserChatDTO
import cohappy.frontend.repository.ChatListRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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

@OptIn(ExperimentalCoroutinesApi::class)
class ChatListViewModelTest {

    private lateinit var viewModel: ChatListViewModel
    private val repository: ChatListRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ChatListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadChats success updates chatsList`() = runTest {
        val userToken = "token123"
        val mockChats = listOf(
            UserChatDTO(chatCode = "C1", name = "Chat 1", participating = listOf("user1", "user2")),
            UserChatDTO(chatCode = "C2", name = "Chat 2", participating = listOf("user1", "user3"))
        )
        coEvery { repository.getUserChats("token123") } returns Result.success(mockChats)

        viewModel.loadChats(userToken)
        
        assertTrue(viewModel.isLoading)
        
        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.isError)
        assertEquals(2, viewModel.chatsList.size)
        assertEquals("C1", viewModel.chatsList[0].id)
        assertEquals("Chat 1", viewModel.chatsList[0].name)
    }

    @Test
    fun `loadChats failure updates error state`() = runTest {
        val userToken = "token123"
        val errorMsg = "Network Error"
        coEvery { repository.getUserChats("token123") } returns Result.failure(Exception(errorMsg))

        viewModel.loadChats(userToken)
        
        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertTrue(viewModel.isError)
        assertEquals(errorMsg, viewModel.errorMessage)
        assertTrue(viewModel.chatsList.isEmpty())
    }

    @Test
    fun `loadChats with blank token does nothing`() = runTest {
        viewModel.loadChats("")
        
        runCurrent()
        
        assertFalse(viewModel.isLoading)
        assertTrue(viewModel.chatsList.isEmpty())
    }

    @Test
    fun `getFilteredChats returns filtered list based on search query`() = runTest {
        val userToken = "token123"
        val mockChats = listOf(
            UserChatDTO(chatCode = "C1", name = "Alice", participating = listOf("user1", "user2")),
            UserChatDTO(chatCode = "C2", name = "Bob", participating = listOf("user1", "user3"))
        )
        coEvery { repository.getUserChats("token123") } returns Result.success(mockChats)

        viewModel.loadChats(userToken)
        advanceUntilIdle()

        viewModel.updateSearchQuery("Ali")
        val filtered = viewModel.getFilteredChats()

        assertEquals(1, filtered.size)
        assertEquals("Alice", filtered[0].name)
    }

    @Test
    fun `updateSearchQuery updates searchQuery state`() {
        viewModel.updateSearchQuery("test query")
        assertEquals("test query", viewModel.searchQuery)
    }
}
