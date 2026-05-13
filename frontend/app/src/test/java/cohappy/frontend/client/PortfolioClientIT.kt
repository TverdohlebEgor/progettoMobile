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
    fun testGetUserPortfolioSuccess() = runTest {
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
                        "category": "DELIVERY_AND_EATING_OUT"
                    }
                ]
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))
        val response = portfolioApi.getUserPortfolio("user_1")

        assertTrue(response.isSuccessful)
        assertEquals(25.5f, response.body()?.debts?.get(0)?.amount)
        assertEquals(DebtType.DELIVERY_AND_EATING_OUT, response.body()?.debts?.get(0)?.debtType)

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
    }

    @Test
    fun testGetUserPortfolioError404() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        val response = portfolioApi.getUserPortfolio("user_1")
        assertFalse(response.isSuccessful)
        assertEquals(404, response.code())
    }

    @Test
    fun testGetUserPortfolioError500() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))
        val response = portfolioApi.getUserPortfolio("user_1")
        assertFalse(response.isSuccessful)
        assertEquals(500, response.code())
    }

    @Test
    fun testGetTotalDebtSuccess() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("45.5"))
        val response = portfolioApi.getUserTotalDebt("user_1")
        assertTrue(response.isSuccessful)
        assertEquals(45.5f, response.body())
    }

    @Test
    fun testGetTotalDebtError400() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(400))
        val response = portfolioApi.getUserTotalDebt("user_1")
        assertFalse(response.isSuccessful)
        assertEquals(400, response.code())
    }

    @Test
    fun testGetTotalCreditsSuccess() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("120.0"))
        val response = portfolioApi.getUserTotalCredits("user_1")
        assertTrue(response.isSuccessful)
        assertEquals(120.0f, response.body())
    }

    @Test
    fun testGetTotalCreditsError500() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))
        val response = portfolioApi.getUserTotalCredits("user_1")
        assertFalse(response.isSuccessful)
        assertEquals(500, response.code())
    }

    @Test
    fun testCreateDebtSuccess() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("NEW_DEBT_ID_999"))
        val dto = CreateDebtDTO("user_1", "user_2", 50.0f, "Spesa", DebtType.GROCERIE)
        val response = portfolioApi.createDebt(dto)

        assertTrue(response.isSuccessful)
        assertEquals("NEW_DEBT_ID_999", response.body())
    }

    @Test
    fun testCreateDebtError400() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(400))
        val dto = CreateDebtDTO("user_1", "user_2", -50.0f, "Spesa", DebtType.GROCERIE)
        val response = portfolioApi.createDebt(dto)

        assertFalse(response.isSuccessful)
        assertEquals(400, response.code())
    }
}