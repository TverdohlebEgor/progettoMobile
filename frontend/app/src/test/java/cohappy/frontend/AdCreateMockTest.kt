//package cohappy.frontend
//
//import cohappy.frontend.client.HouseApiClient
//import cohappy.frontend.client.UserApiClient
//import cohappy.frontend.client.dto.HouseStateEnum
//import cohappy.frontend.client.dto.request.CreateHouseAdvertisementDTO
//import kotlinx.coroutines.test.runTest
//import okhttp3.mockwebserver.MockResponse
//import okhttp3.mockwebserver.MockWebServer
//import org.junit.After
//import org.junit.Assert
//import org.junit.Assert.assertTrue
//import org.junit.Before
//import retrofit2.Retrofit
//import retrofit2.converter.moshi.MoshiConverterFactory
//import retrofit2.converter.scalars.ScalarsConverterFactory
//import kotlin.test.Test
//import kotlin.test.assertEquals
//
//class AdCreateMockTest {
//    private lateinit var mockWebServer: MockWebServer
//    private lateinit var testHouseApi: HouseApiClient
//
//    @Before
//    fun setup() {
//        mockWebServer = MockWebServer()
//        mockWebServer.start()
//
//        // We create a temporary Retrofit instance pointing to the Mock server
//        val retrofit = Retrofit.Builder()
//            .baseUrl(mockWebServer.url("/"))
//            .addConverterFactory(ScalarsConverterFactory.create())
//            .addConverterFactory(MoshiConverterFactory.create())
//            .build()
//
//        testHouseApi = retrofit.create(HouseApiClient::class.java)
//    }
//
//    @After
//    fun teardown() {
//        mockWebServer.shutdown()
//    }
//
//    @Test
//    fun `create advertisement returns success`() = runTest {
//        val fakeHouseCode="HOUS-12345"
//        mockWebServer.enqueue(
//            MockResponse()
//                .setResponseCode(200)
//                .setBody(fakeHouseCode)
//        )
//
//        val request = CreateHouseAdvertisementDTO(fakeHouseCode,HouseStateEnum.PUBLIC, "test@cohappy.com", "test")
//        val response = testHouseApi.createHouseAdvertisement(request)
//
//        assertTrue(response.isSuccessful)
//        assertEquals(fakeHouseCode, response.body())
//
//        val recordedRequest = mockWebServer.takeRequest()
//        Assert.assertEquals("/api/house/advertisement/create", recordedRequest.path)
//        Assert.assertEquals("POST", recordedRequest.method)
//
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
