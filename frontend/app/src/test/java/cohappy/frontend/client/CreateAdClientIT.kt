package cohappy.frontend.client

import cohappy.frontend.client.dto.enum.HouseStateEnum
import cohappy.frontend.client.dto.request.CreateHouseAdvertisementDTO
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

class CreateAdClientIT {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var houseApi: HouseApiClient

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val moshi = Moshi.Builder()
            .add(LocalDateAdapter())
            .add(LocalDateTimeAdapter())
            .add(ByteArrayAdapter())
            .build()

        houseApi = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .build()
            .create(HouseApiClient::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `createHouseAdvertisement happy path returns success ID`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("NEW_AD_123"))

        val dto = CreateHouseAdvertisementDTO(
            houseCode = "H1",
            state = HouseStateEnum.PUBLIC,
            publishedBy = "U1",
            description = "Bella stanza",
            images = emptyList()
        )

        val response = houseApi.createHouseAdvertisement(dto)

        assertTrue(response.isSuccessful)
        assertEquals("NEW_AD_123", response.body())

        val request = mockWebServer.takeRequest()
        assertEquals("POST", request.method)
        assertEquals("/api/house/advertisement/create", request.path)
    }

    @Test
    fun `createHouseAdvertisement unhappy path 400 returns failure`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(400))

        val dto = CreateHouseAdvertisementDTO(
            houseCode = "",
            state = HouseStateEnum.PRIVATE,
            publishedBy = "",
            description = ""
        )

        val response = houseApi.createHouseAdvertisement(dto)

        assertFalse(response.isSuccessful)
        assertEquals(400, response.code())
    }

    @Test
    fun `createHouseAdvertisement unhappy path 500 returns server error`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(500))

        val dto = CreateHouseAdvertisementDTO(
            houseCode = "H1",
            state = HouseStateEnum.PUBLIC,
            publishedBy = "U1",
            description = "Test",
            images = null
        )
        val response = houseApi.createHouseAdvertisement(dto)

        assertFalse(response.isSuccessful)
        assertEquals(500, response.code())
    }
}