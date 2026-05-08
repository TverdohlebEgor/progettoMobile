package cohappy.frontend.repository

import cohappy.frontend.client.ChatApiClient
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.CreateChatDTO
import cohappy.frontend.client.dto.response.UserChatDTO
import cohappy.frontend.expections.ErrorMessages.SERVER_ERROR
import cohappy.frontend.expections.ErrorMessages.USER_NOT_FOUND_CREATE_CHAT
import cohappy.frontend.expections.ErrorMessages.USER_NOT_FOUND_GET_CHATS
import cohappy.frontend.expections.NotFoundException
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

class ChatListRepositoryTest {

    private val chatApi = mockk<ChatApiClient>()
    private lateinit var repository: ChatListRepository

    @Before
    fun setup() {
        mockkObject(ClientSingleton)
        every { ClientSingleton.chatApi } returns chatApi
        repository = ChatListRepository()
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `createChat success returns success Result`() = runTest {
        val dto = CreateChatDTO(participating = listOf("user1", "user2"), name = "Chat Name")
        coEvery { chatApi.createChat(dto) } returns Response.success("CHAT_ID")

        val result = repository.createChat(dto)

        assertTrue(result.isSuccess)
        assertEquals("CHAT_ID", result.getOrNull())
    }

    @Test
    fun `createChat failure 404 returns NotFoundException`() = runTest {
        val dto = CreateChatDTO(participating = listOf("user1", "user2"), name = "Chat Name")
        coEvery { chatApi.createChat(dto) } returns Response.error(404, "".toResponseBody())

        val result = repository.createChat(dto)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotFoundException)
        assertEquals(USER_NOT_FOUND_CREATE_CHAT, result.exceptionOrNull()?.message)
    }

    @Test
    fun `createChat failure 500 returns ServerErrorException`() = runTest {
        val dto = CreateChatDTO(participating = listOf("user1", "user2"), name = "Chat Name")
        coEvery { chatApi.createChat(dto) } returns Response.error(500, "".toResponseBody())

        val result = repository.createChat(dto)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ServerErrorException)
        assertEquals(SERVER_ERROR, result.exceptionOrNull()?.message)
    }

    @Test
    fun `createChat exception returns failure`() = runTest {
        val dto = CreateChatDTO(participating = listOf("user1", "user2"), name = "Chat Name")
        val exception = Exception("Network Error")
        coEvery { chatApi.createChat(dto) } throws exception

        val result = repository.createChat(dto)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `getUserChats success returns list of chats`() = runTest {
        val userId = "user123"
        val mockChats = listOf(UserChatDTO(chatCode = "chat1", name = "Chat 1"))
        coEvery { chatApi.getUserChats(userId) } returns Response.success(mockChats)

        val result = repository.getUserChats(userId)

        assertTrue(result.isSuccess)
        assertEquals(mockChats, result.getOrNull())
    }

    @Test
    fun `getUserChats failure 404 returns NotFoundException`() = runTest {
        val userId = "user123"
        coEvery { chatApi.getUserChats(userId) } returns Response.error(404, "".toResponseBody())

        val result = repository.getUserChats(userId)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotFoundException)
        assertEquals(USER_NOT_FOUND_GET_CHATS, result.exceptionOrNull()?.message)
    }

    @Test
    fun `getUserChats failure 500 returns ServerErrorException`() = runTest {
        val userId = "user123"
        coEvery { chatApi.getUserChats(userId) } returns Response.error(500, "".toResponseBody())

        val result = repository.getUserChats(userId)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ServerErrorException)
        assertEquals(SERVER_ERROR, result.exceptionOrNull()?.message)
    }

    @Test
    fun `getUserChats success with null body returns ServerErrorException`() = runTest {
        val userId = "user123"
        coEvery { chatApi.getUserChats(userId) } returns Response.success(null)

        val result = repository.getUserChats(userId)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ServerErrorException)
        assertEquals(SERVER_ERROR, result.exceptionOrNull()?.message)
    }

    @Test
    fun `getUserChats exception returns failure`() = runTest {
        val userId = "user123"
        val exception = Exception("Network Error")
        coEvery { chatApi.getUserChats(userId) } throws exception

        val result = repository.getUserChats(userId)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
