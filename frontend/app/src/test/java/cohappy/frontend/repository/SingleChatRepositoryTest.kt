package cohappy.frontend.repository

import cohappy.frontend.client.ChatApiClient
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.AddMessageDTO
import cohappy.frontend.client.dto.response.ChatMessageDTO
import cohappy.frontend.expections.ErrorMessages.CHAT_NOT_FOUND
import cohappy.frontend.expections.ErrorMessages.SERVER_ERROR
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

class SingleChatRepositoryTest {

    private val chatApi = mockk<ChatApiClient>()
    private lateinit var repository: SingleChatRepository

    @Before
    fun setup() {
        mockkObject(ClientSingleton)
        every { ClientSingleton.chatApi } returns chatApi
        repository = SingleChatRepository()
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    // --- getMessages Tests ---

    @Test
    fun `getMessages success returns list of messages`() = runTest {
        val chatId = "chat123"
        val mockMessages = listOf(ChatMessageDTO(message = "Hello", userCode = "user1"))
        coEvery { chatApi.getMessages(chatId) } returns Response.success(mockMessages)

        val result = repository.getMessages(chatId)

        assertTrue(result.isSuccess)
        assertEquals(mockMessages, result.getOrNull())
    }

    @Test
    fun `getMessages failure 404 returns NotFoundException`() = runTest {
        val chatId = "chat123"
        coEvery { chatApi.getMessages(chatId) } returns Response.error(404, "".toResponseBody())

        val result = repository.getMessages(chatId)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotFoundException)
        assertEquals(CHAT_NOT_FOUND, result.exceptionOrNull()?.message)
    }

    @Test
    fun `getMessages failure 500 returns ServerErrorException`() = runTest {
        val chatId = "chat123"
        coEvery { chatApi.getMessages(chatId) } returns Response.error(500, "".toResponseBody())

        val result = repository.getMessages(chatId)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ServerErrorException)
        assertEquals(SERVER_ERROR, result.exceptionOrNull()?.message)
    }

    @Test
    fun `getMessages exception returns failure`() = runTest {
        val chatId = "chat123"
        val exception = Exception("Network Error")
        coEvery { chatApi.getMessages(chatId) } throws exception

        val result = repository.getMessages(chatId)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // --- sendMessage Tests ---

    @Test
    fun `sendMessage success returns success Result`() = runTest {
        val dto = AddMessageDTO(chatCode = "chat123", message = "Hello", userCode = "user1")
        coEvery { chatApi.addMessage(dto) } returns Response.success("OK")

        val result = repository.sendMessage(dto)

        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
    }

    @Test
    fun `sendMessage failure 404 returns NotFoundException`() = runTest {
        val dto = AddMessageDTO(chatCode = "chat123", message = "Hello", userCode = "user1")
        coEvery { chatApi.addMessage(dto) } returns Response.error(404, "".toResponseBody())

        val result = repository.sendMessage(dto)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotFoundException)
        assertEquals(CHAT_NOT_FOUND, result.exceptionOrNull()?.message)
    }

    @Test
    fun `sendMessage failure 500 returns ServerErrorException`() = runTest {
        val dto = AddMessageDTO(chatCode = "chat123", message = "Hello", userCode = "user1")
        coEvery { chatApi.addMessage(dto) } returns Response.error(500, "".toResponseBody())

        val result = repository.sendMessage(dto)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ServerErrorException)
        assertEquals(SERVER_ERROR, result.exceptionOrNull()?.message)
    }

    @Test
    fun `sendMessage exception returns failure`() = runTest {
        val dto = AddMessageDTO(chatCode = "chat123", message = "Hello", userCode = "user1")
        val exception = Exception("Network Error")
        coEvery { chatApi.addMessage(dto) } throws exception

        val result = repository.sendMessage(dto)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
