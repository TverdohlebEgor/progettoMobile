package cohappy.frontend.client

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

class AdListClientIT {

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
    fun `getAllHouseAdvertisements returns list on success`() = runTest {
        val json = """
            [
                {
                    "houseCode": "HOUSE123",
                    "costPerMonth": 500,
                    "country": "Italy",
                    "region": "Lazio",
                    "street": "Via Roma",
                    "civicNumber": 1,
                    "state": "PUBLIC",
                    "publishedByCode": "USER123",
                    "description": "Bella casa"
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(json)
        )

        val response = testHouseApi.getAllHouseAdvertisements()

        Assert.assertTrue(response.isSuccessful)
        Assert.assertEquals(1, response.body()?.size)
        Assert.assertEquals("HOUSE123", response.body()?.first()?.houseCode)
        Assert.assertEquals(500, response.body()?.first()?.costPerMonth)
    }

    @Test
    fun `getAllHouseAdvertisements returns 500 error`() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
        )

        val response = testHouseApi.getAllHouseAdvertisements()

        Assert.assertFalse(response.isSuccessful)
        Assert.assertEquals(500, response.code())
    }
}
