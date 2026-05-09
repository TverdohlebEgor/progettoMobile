package cohappy.frontend.client

import cohappy.frontend.client.dto.request.AddMessageDTO
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.LocalDateTime

class SingleChatClientIT {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var testChatApi: ChatApiClient

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val moshi = Moshi.Builder()
            .add(LocalDateAdapter())
            .add(LocalDateTimeAdapter())
            .add(ByteArrayAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        testChatApi = retrofit.create(ChatApiClient::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getMessages returns list on success`() = runTest {
        val now = LocalDateTime.now().withNano(0).toString()
        val json = """
            [
                {
                    "message": "Hello from IT",
                    "userCode": "USER1",
                    "timestamp": "$now"
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(json)
        )

        val response = testChatApi.getMessages("CHAT123")

        Assert.assertTrue(response.isSuccessful)
        Assert.assertEquals(1, response.body()?.size)
        Assert.assertEquals("Hello from IT", response.body()?.first()?.message)
    }

    @Test
    fun `getMessages returns 404 error`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
        )

        val response = testChatApi.getMessages("CHAT123")

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(404, response.code())
    }

    @Test
    fun `getMessages returns 500 error`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
        )

        val response = testChatApi.getMessages("CHAT123")

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(500, response.code())
    }

    @Test
    fun `addMessage returns success`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("Message added successfully")
        )

        val request = AddMessageDTO(
            chatCode = "CHAT123",
            message = "New message",
            userCode = "USER1"
        )

        val response = testChatApi.addMessage(request)

        Assert.assertTrue(response.isSuccessful)
        Assert.assertEquals("Message added successfully", response.body())
    }

    @Test
    fun `addMessage returns 400 error`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(400)
        )

        val request = AddMessageDTO(
            chatCode = "CHAT123",
            message = "",
            userCode = "USER1"
        )

        val response = testChatApi.addMessage(request)

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(400, response.code())
    }

    @Test
    fun `addMessage returns 404 error`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
        )

        val request = AddMessageDTO(
            chatCode = "NON_EXISTENT_CHAT",
            message = "Hello",
            userCode = "USER1"
        )

        val response = testChatApi.addMessage(request)

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(404, response.code())
    }

    @Test
    fun `addMessage returns 500 error`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
        )

        val request = AddMessageDTO(
            chatCode = "CHAT123",
            message = "Hello",
            userCode = "USER1"
        )

        val response = testChatApi.addMessage(request)

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(500, response.code())
    }
}
