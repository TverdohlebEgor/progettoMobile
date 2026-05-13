package cohappy.frontend.client

import cohappy.frontend.client.dto.response.GetHouseDTO
import cohappy.frontend.client.dto.response.GetNextChoreDTO
import cohappy.frontend.client.dto.response.GetNotificationDTO
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
import java.time.LocalDate
import java.time.LocalDateTime

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
    fun testGetHouseDetailsSuccess() = runTest {
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

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val response = houseApi.getHouse("COH-8X2P")

        assertTrue(response.isSuccessful)
        assertEquals("Via Roma", response.body()?.street)
        assertEquals(12, response.body()?.civicNumber)

        val request = mockWebServer.takeRequest()
        assertEquals("/api/house/COH-8X2P", request.path)
        assertEquals("GET", request.method)
    }

    @Test
    fun testGetNotificationsSuccess() = runTest {
        val jsonResponse = """
            [
                {
                    "eventId": "NOTIF_1",
                    "eventType": "CHAT",
                    "title": "Nuovo Messaggio",
                    "subtitle": "Hai un nuovo messaggio da Egor",
                    "timestamp": "2026-05-11",
                    "userCode": "user_123"
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val response = notificationApi.getUserNotifications("user_123")

        assertTrue(response.isSuccessful)
        assertEquals(1, response.body()?.size)
        assertEquals("CHAT", response.body()?.get(0)?.eventType)
        assertEquals("Nuovo Messaggio", response.body()?.get(0)?.title)

        val request = mockWebServer.takeRequest()
        assertEquals("/api/notifications/user_123", request.path)
    }

    @Test
    fun testGetNextChoreSuccess() = runTest {
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

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(jsonResponse)
        )

        val targetDate = LocalDate.parse("2026-05-12")
        val response = choreApi.getNextUserChore("user_123", targetDate)

        assertTrue(response.isSuccessful)
        val chore = response.body()?.firstOrNull()
        assertEquals("Spazzare a terra", chore?.name)
        assertEquals(targetDate, chore?.date)

        val request = mockWebServer.takeRequest()
        assertEquals("/api/chore/next/user_123/2026-05-12", request.path)
    }

    @Test
    fun testGetNextChoreNoContent() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(204)
        )

        val targetDate = LocalDate.parse("2026-05-12")
        val response = choreApi.getNextUserChore("user_123", targetDate)

        assertTrue(response.isSuccessful)
        assertEquals(204, response.code())
        assertNull(response.body())
    }

    @Test
    fun testGetTotalDebtSuccess() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("45.5")
        )

        val response = portfolioApi.getUserTotalDebt("user_123")

        assertTrue(response.isSuccessful)
        assertEquals(45.5f, response.body() ?: 0.0f, 0.001f)

        val request = mockWebServer.takeRequest()
        assertEquals("/api/debt/user_123/total", request.path)
    }

    @Test
    fun testHouseApiErrorHandling() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
        )

        val response = houseApi.getHouse("INVALID_CODE")

        assertFalse(response.isSuccessful)
        assertEquals(404, response.code())
    }
}