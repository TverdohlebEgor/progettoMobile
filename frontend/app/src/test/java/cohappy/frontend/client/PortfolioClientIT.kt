package cohappy.frontend.client

import cohappy.frontend.client.dto.enum.DebtType
import cohappy.frontend.client.dto.request.CreateDebtDTO
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class PortfolioClientIT {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: PortfolioApiClient

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        api = retrofit.create(PortfolioApiClient::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    // --- HAPPY PATHS ---

    @Test
    fun `getUserPortfolio - Happy Path - Ritorna DTO correttamente deserializzato`() = runBlocking {
        val jsonResponse = """
            {
                "debts": [
                    {
                        "debtId": "D123",
                        "debtorsUserCode": {"U1": false},
                        "creditorUserCode": "U2",
                        "amount": 25.50,
                        "description": "Sushi",
                        "debtType": "DELIVERY_AND_EATING_OUT"
                    }
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val response = api.getUserPortfolio("U1")

        assertTrue(response.isSuccessful)
        val body = response.body()
        assertEquals(1, body?.debts?.size)
        assertEquals("D123", body?.debts?.get(0)?.debtId)
        assertEquals(25.50f, body?.debts?.get(0)?.amount)
        assertEquals(DebtType.DELIVERY_AND_EATING_OUT, body?.debts?.get(0)?.debtType)
    }

    @Test
    fun `createDebt - Happy Path - Invia request corretta e riceve 200`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("\"DEBT_CREATED\""))

        val request = CreateDebtDTO(
            creditorCode = "ME",
            receiverCode = mapOf("YOU" to false),
            amount = 10f,
            description = "Test",
            debtType = DebtType.OTHER
        )

        val response = api.createDebt(request)

        assertTrue(response.isSuccessful)

        // Verifica che la request mandata al server fosse giusta
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("POST", recordedRequest.method)
        assertEquals("/api/portafolio/debt/create", recordedRequest.path)
        assertTrue(recordedRequest.body.readUtf8().contains("creditorCode\":\"ME\""))
    }

    // --- BAD PATHS ---

    @Test
    fun `getUserTotalDebt - Bad Path - Ritorna 404 senza esplodere`() = runBlocking {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))

        val response = api.getUserTotalDebt("U_UNKNOWN")

        assertTrue(!response.isSuccessful)
        assertEquals(404, response.code())
    }
}