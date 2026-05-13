package cohappy.frontend.client

import cohappy.frontend.client.dto.response.GetHouseDTO
import cohappy.frontend.client.dto.response.GetNextChoreDTO
import cohappy.frontend.client.dto.response.GetNotificationDTO
import java.time.LocalDate
import com.squareup.moshi.Moshi
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class HouseDashboardClientIT {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var houseApi: HouseApiClient
    private lateinit var notificationApi: NotificationApiClient
    private lateinit var choreApi: ChoreApiClient
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

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .build()

        houseApi = retrofit.create(HouseApiClient::class.java)
        notificationApi = retrofit.create(NotificationApiClient::class.java)
        choreApi = retrofit.create(ChoreApiClient::class.java)
        portfolioApi = retrofit.create(PortfolioApiClient::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getHouse success returns GetHouseDTO`() = runTest {
        val jsonResponse = """
            {
                "houseCode": "COH-8X2P",
                "street": "Via Roma",
                "civicNumber": 12,
                "country": "Italia",
                "region": "Lombardia",
                "costPerMonth": 450.0,
                "state": "ACTIVE"
            }
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))
        val response = houseApi.getHouse("COH-8X2P")

        assertTrue(response.isSuccessful)
        assertEquals("Via Roma", response.body()?.street)
        assertEquals(12, response.body()?.civicNumber)
    }

    @Test
    fun `getHouse error 404 returns failure`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        val response = houseApi.getHouse("INVALID_CODE")
        assertFalse(response.isSuccessful)
        assertEquals(404, response.code())
    }

    @Test
    fun `getNotifications success returns list`() = runTest {
        val jsonResponse = """
            [
                {
                    "eventId": "NOTIF_1",
                    "eventType": "CHAT",
                    "title": "Nuovo Messaggio",
                    "subtitle": "Hai un nuovo messaggio",
                    "timestamp": "2026-05-11",
                    "userCode": "user_123"
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))
        val response = notificationApi.getUserNotifications("user_123")

        assertTrue(response.isSuccessful)
        assertEquals(1, response.body()?.size)
        assertEquals("CHAT", response.body()?.get(0)?.eventType)
    }

    // 💅 AGGIUNTO: Unhappy path per le notifiche
    @Test
    fun `getNotifications error 500 returns failure`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))
        val response = notificationApi.getUserNotifications("user_123")
        assertFalse(response.isSuccessful)
        assertEquals(500, response.code())
    }

    @Test
    fun `getNextChore success returns DTO`() = runTest {
        val jsonResponse = """
            [
                {
                    "choreCode": "CHORE_99",
                    "name": "Spazzare a terra",
                    "assignedTo": "user_123",
                    "date": "2026-05-12",
                    "completed": "false"
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))
        val response = choreApi.getNextUserChore("user_123", LocalDate.parse("2026-05-12"))

        assertTrue(response.isSuccessful)
        assertEquals("Spazzare a terra", response.body()?.get(0)?.name)
    }

    @Test
    fun `getNextChore 204 No Content returns null body`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(204))
        val response = choreApi.getNextUserChore("user_123", LocalDate.parse("2026-05-12"))

        assertTrue(response.isSuccessful)
        assertEquals(204, response.code())
        assertNull(response.body())
    }

    // 💅 AGGIUNTO: Unhappy path per la prossima faccenda
    @Test
    fun `getNextChore error 500 returns failure`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))
        val response = choreApi.getNextUserChore("user_123", LocalDate.parse("2026-05-12"))
        assertFalse(response.isSuccessful)
        assertEquals(500, response.code())
    }

    @Test
    fun `getTotalDebt success returns double`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("45.5"))
        val response = portfolioApi.getUserTotalDebt("user_123")

        assertTrue(response.isSuccessful)
        assertEquals(45.5f, response.body() ?: 0.0f, 0.001f)
    }

    @Test
    fun `getTotalDebt error 500 returns failure`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))
        val response = portfolioApi.getUserTotalDebt("user_123")
        assertFalse(response.isSuccessful)
        assertEquals(500, response.code())
    }
}