package cohappy.frontend.client

import cohappy.frontend.client.dto.enum.DebtType
import cohappy.frontend.client.dto.request.CreateDebtDTO
import com.squareup.moshi.Moshi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class PortfolioClientIT {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var portfolioApi: PortfolioApiClient

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()


        val moshi = Moshi.Builder()
            .add(LocalDateAdapter())
            .add(LocalDateTimeAdapter())
            .add(ByteArrayAdapter())
            .build()

        portfolioApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .build()
            .create(PortfolioApiClient::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `get user portfolio returns success and correct path`() = runTest {
        val jsonResponse = """
            {
                "monthlyTransactions": [],
                "debts": [
                    {
                        "debtId": "DEBT_123",
                        "debtorUserCode": "user_1",
                        "beneficiaryUserCode": "user_2",
                        "amount": 25.5,
                        "description": "Sushi Delivery",
                        "debtType": "DELIVERY_AND_EATING_OUT"
                    }
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(jsonResponse)
        )

        val response = portfolioApi.getUserPortfolio("user_1")

        assertTrue(response.isSuccessful)
        assertEquals(25.5f, response.body()?.debts?.get(0)?.amount)

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/portafolio/user_1", recordedRequest.path)
        assertEquals("GET", recordedRequest.method)
    }

    @Test
    fun `create debt returns success and correct path`() = runTest {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody("NEW_DEBT_ID_999")
        )

        val dto = CreateDebtDTO(
            senderUserCode = "user_1",
            receiverUserCode = "user_2",
            amount = 50.0f,
            description = "Spesa Esselunga",
            debtType = DebtType.GROCERIE
        )

        val response = portfolioApi.createDebt(dto)

        assertTrue(response.isSuccessful)
        assertEquals("NEW_DEBT_ID_999", response.body())

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/portafolio/debt/create", recordedRequest.path)
        assertEquals("POST", recordedRequest.method)
    }

    @Test
    fun `getUserPortfolio returns 500 error`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))
        val response = portfolioApi.getUserPortfolio("user_1")
        assertFalse(response.isSuccessful)
        assertEquals(500, response.code())
    }

    @Test
    fun `getUserPortfolio returns 404 error`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        val response = portfolioApi.getUserPortfolio("user_1")
        assertFalse(response.isSuccessful)
        assertEquals(404, response.code())
    }

    @Test
    fun `getUserPortfolio returns 400 error`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(400))
        val response = portfolioApi.getUserPortfolio("user_1")
        assertFalse(response.isSuccessful)
        assertEquals(400, response.code())
    }
}