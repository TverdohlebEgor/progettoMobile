package cohappy.frontend.client

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import cohappy.frontend.client.UserApiClient
import cohappy.frontend.client.dto.request.LoginDTO
import retrofit2.converter.scalars.ScalarsConverterFactory

class LoginMockTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var testUserApi: UserApiClient

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        // We create a temporary Retrofit instance pointing to the Mock server
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
        // 1. Tell the Mock server what to return when called
        val fakeUserCode = "USR-12345"
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(fakeUserCode)
        )

        // 2. Execute the call
        val request = LoginDTO("test@cohappy.com", "3200147723", "password")
        val response = testUserApi.login(request)

        // 3. Verify the results
        assertTrue(response.isSuccessful)
        assertEquals(fakeUserCode, response.body())

        // Verify the request sent to the server was correct
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/user/login", recordedRequest.path)
        assertEquals("POST", recordedRequest.method)
    }
}