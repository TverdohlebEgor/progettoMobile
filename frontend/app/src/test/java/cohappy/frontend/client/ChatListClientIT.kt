package cohappy.frontend.client

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

class ChatListClientIT {

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
    fun `getUserChats returns list on success`() = runTest {
        val json = """
            [
                {
                    "chatCode": "CHAT123",
                    "name": "General Chat",
                    "participating": ["user1", "user2"]
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(json)
        )

        val response = testChatApi.getUserChats("user1")

        Assert.assertTrue(response.isSuccessful)
        Assert.assertEquals(1, response.body()?.size)
        Assert.assertEquals("CHAT123", response.body()?.first()?.chatCode)
    }

    @Test
    fun `getUserChats returns 404 error`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
        )

        val response = testChatApi.getUserChats("user1")

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(404, response.code())
    }

    @Test
    fun `getUserChats returns 500 error`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
        )

        val response = testChatApi.getUserChats("user1")

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(500, response.code())
    }

    @Test
    fun `getMessages returns list on success`() = runTest {
        val now = LocalDateTime.now().withNano(0).toString()
        val json = """
            [
                {
                    "message": "Hello world",
                    "userCode": "user2",
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
        Assert.assertEquals("Hello world", response.body()?.first()?.message)
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
}
