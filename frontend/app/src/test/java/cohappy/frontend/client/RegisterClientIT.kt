package cohappy.frontend.client

import cohappy.frontend.client.dto.request.RegisterDTO
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

class RegisterClientIT {

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
    fun `register returns success user code`() = runTest {
        val fakeUserCode = "USR-REGISTER-123"
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(fakeUserCode)
        )

        val request = RegisterDTO(
            name = "Mario",
            surname = "Rossi",
            birthDate = "1990-01-01",
            email = "mario.rossi@example.com",
            phoneNumber = "1234567890",
            password = "securePassword123"
        )
        val response = testUserApi.register(request)

        Assert.assertTrue(response.isSuccessful)
        Assert.assertEquals(fakeUserCode, response.body())

        val recordedRequest = mockWebServer.takeRequest()
        Assert.assertEquals("/api/user/register", recordedRequest.path)
        Assert.assertEquals("POST", recordedRequest.method)
    }

    @Test
    fun `register returns 400 error`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(400)
        )

        val request = RegisterDTO(
            name = "Mario",
            surname = "Rossi",
            birthDate = "1990-01-01",
            email = "invalid-email",
            phoneNumber = "1234567890",
            password = "123"
        )
        val response = testUserApi.register(request)

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(400, response.code())
    }

    @Test
    fun `register returns 500 error`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
        )

        val request = RegisterDTO(
            name = "Mario",
            surname = "Rossi",
            birthDate = "1990-01-01",
            email = "mario.rossi@example.com",
            phoneNumber = "1234567890",
            password = "securePassword123"
        )
        val response = testUserApi.register(request)

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(500, response.code())
    }
}
