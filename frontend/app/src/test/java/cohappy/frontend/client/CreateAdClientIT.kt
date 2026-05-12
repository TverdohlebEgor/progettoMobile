package cohappy.frontend.client

import cohappy.frontend.client.dto.enum.HouseStateEnum
import cohappy.frontend.client.dto.request.CreateHouseAdvertisementDTO
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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

class CreateAdClientIT {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var testHouseApi: HouseApiClient

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val moshi = Moshi.Builder()
            .add(LocalDateAdapter())
            .add(LocalDateTimeAdapter())
            .add(ByteArrayAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        testHouseApi = retrofit.create(HouseApiClient::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `createHouseAdvertisement returns success`() = runTest {
        val request = CreateHouseAdvertisementDTO(
            houseCode = "HOUSE123",
            state = HouseStateEnum.PUBLIC,
            publishedBy = "USER123",
            description = "Bella casa"
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("Advertisement created successfully")
        )

        val response = testHouseApi.createHouseAdvertisement(request)

        Assert.assertTrue(response.isSuccessful)
        Assert.assertEquals("Advertisement created successfully", response.body())
    }

    @Test
    fun `createHouseAdvertisement returns 400 bad request`() = runTest {
        val request = CreateHouseAdvertisementDTO(
            houseCode = "",
            state = HouseStateEnum.PUBLIC,
            publishedBy = "USER123"
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(400)
        )

        val response = testHouseApi.createHouseAdvertisement(request)

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(400, response.code())
    }

    @Test
    fun `createHouseAdvertisement returns 404 not found`() = runTest {
        val request = CreateHouseAdvertisementDTO(
            houseCode = "UNKNOWN",
            state = HouseStateEnum.PUBLIC,
            publishedBy = "USER123"
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
        )

        val response = testHouseApi.createHouseAdvertisement(request)

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(404, response.code())
    }

    @Test
    fun `createHouseAdvertisement returns 500 error`() = runTest {
        val request = CreateHouseAdvertisementDTO(
            houseCode = "HOUSE123",
            state = HouseStateEnum.PUBLIC,
            publishedBy = "USER123"
        )

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
        )

        val response = testHouseApi.createHouseAdvertisement(request)

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(500, response.code())
    }
}
