package cohappy.frontend.client

import cohappy.frontend.client.dto.request.LoginDTO
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

class LoginClientIT {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var testUserApi: UserApiClient

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        testUserApi = retrofit.create(UserApiClient::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `login returns user code on success`() = runTest {
        val fakeUserCode = "USR-12345"
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(fakeUserCode)
        )

        val request = LoginDTO("test@cohappy.com", "3200147723", "password")
        val response = testUserApi.login(request)

        Assert.assertTrue(response.isSuccessful)
        Assert.assertEquals(fakeUserCode, response.body())

        val recordedRequest = mockWebServer.takeRequest()
        Assert.assertEquals("/api/user/login", recordedRequest.path)
        Assert.assertEquals("POST", recordedRequest.method)
    }

    @Test
    fun `login returns 500 error`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
        )

        val request = LoginDTO("test@cohappy.com", "3200147723", "password")
        val response = testUserApi.login(request)

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(500, response.code())
    }

    @Test
    fun `login returns 404 error`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
        )

        val request = LoginDTO("test@cohappy.com", "3200147723", "password")
        val response = testUserApi.login(request)

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(404, response.code())
    }

    @Test
    fun `login returns 400 error`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(400)
        )

        val request = LoginDTO("test@cohappy.com", "3200147723", "password")
        val response = testUserApi.login(request)

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(400, response.code())
    }
}
