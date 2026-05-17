package cohappy.frontend.client

import cohappy.frontend.client.dto.request.CreateChoreDTO
import cohappy.frontend.client.dto.request.PatchChoreDTO
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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class ChoreClientIT {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var choreApi: ChoreApiClient

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val moshi = Moshi.Builder()
            .add(LocalDateAdapter())
            .add(LocalDateTimeAdapter())
            .add(ByteArrayAdapter())
            .build()

        choreApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(ChoreApiClient::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getChore happy path returns list of GetChoreDTO`() = runTest {
        val jsonResponse = """
            [
                {
                    "choreCode": "CHORE_1",
                    "assignedTo": "USR_123",
                    "assignedToName": "Ale",
                    "completed": false,
                    "name": "Pulizia Bagno",
                    "description": "Specchio e sanitari",
                    "deadline": "2026-05-15"
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(jsonResponse))

        val response = choreApi.getChore("HOUSE_1", LocalDate.parse("2026-05-12"))

        assertTrue(response.isSuccessful)
        assertEquals(1, response.body()?.size)
        assertEquals("Pulizia Bagno", response.body()?.get(0)?.name)

        val request = mockWebServer.takeRequest()
        assertEquals("GET", request.method)
    }

    @Test
    fun `getChore unhappy path 404 returns failure`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        val response = choreApi.getChore("HOUSE_INVALID", LocalDate.parse("2026-05-12"))

        assertFalse(response.isSuccessful)
        assertEquals(404, response.code())
    }

    @Test
    fun `patchChore happy path returns success string`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("OK"))

        val dto = PatchChoreDTO(choreCode = "CHORE_1", completed = true)
        val response = choreApi.patchChore(dto)

        assertTrue(response.isSuccessful)
        assertEquals("OK", response.body())
    }

    @Test
    fun `patchChore unhappy path 500 returns failure`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val dto = PatchChoreDTO(choreCode = "CHORE_1", completed = true)
        val response = choreApi.patchChore(dto)

        assertFalse(response.isSuccessful)
        assertEquals(500, response.code())
    }

    @Test
    fun `createChore happy path returns success id`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("NEW_CHORE_99"))

        val date = LocalDate.parse("2026-05-20")
        val dto = CreateChoreDTO(
            name = "Cucina",
            description = "Pulire frigo",
            assignedTo = mapOf(date to "USR_1"),
            days = listOf(date),
            houseCode = "H1",
            createdBy = "USR_1"
        )
        val response = choreApi.createChore(dto)

        assertTrue(response.isSuccessful)
        assertEquals("NEW_CHORE_99", response.body())
    }

    @Test
    fun `createChore unhappy path 400 returns failure`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(400))

        val dto = CreateChoreDTO(
            name = "",
            description = "",
            assignedTo = emptyMap(),
            days = emptyList(),
            houseCode = "",
            createdBy = ""
        )
        val response = choreApi.createChore(dto)

        assertFalse(response.isSuccessful)
        assertEquals(400, response.code())
    }
}
