package cohappy.frontend.client

import cohappy.frontend.client.dto.request.ModifyHouseDTO
import cohappy.frontend.client.dto.request.PatchUserDTO
import cohappy.frontend.client.dto.request.RemoveUserDTO
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

class HouseProfileClientIT {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var houseApi: HouseApiClient
    private lateinit var userApi: UserApiClient

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
        userApi = retrofit.create(UserApiClient::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getHouse happy path returns GetHouseDTO`() = runTest {
        val json = """{"houseCode":"H1","street":"Via Roma","civicNumber":10,"admins":["U1"],"users":["U2"]}"""
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody(json))

        val resp = houseApi.getHouse("H1")
        assertTrue(resp.isSuccessful)
        assertEquals("Via Roma", resp.body()?.street)
    }

    @Test
    fun `getHouse unhappy path 404 returns failure`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(404))
        val resp = houseApi.getHouse("H_MISSING")
        assertFalse(resp.isSuccessful)
        assertEquals(404, resp.code())
    }

    @Test
    fun `modifyHouse happy path returns success`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("Updated"))
        val dto = ModifyHouseDTO(houseCode = "H1", newHouseCode = "H2")
        val resp = houseApi.modifyHouse(dto)
        assertTrue(resp.isSuccessful)
    }

    @Test
    fun `modifyHouse unhappy path 409 duplicate code`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(409).setBody("Already exists"))
        val dto = ModifyHouseDTO(houseCode = "H1", newHouseCode = "EXISTING")
        val resp = houseApi.modifyHouse(dto)
        assertFalse(resp.isSuccessful)
        assertEquals(409, resp.code())
    }

    @Test
    fun `removeUser happy path returns success`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("OK"))
        val dto = RemoveUserDTO("H1", "U1")
        val resp = houseApi.removeUser(dto)
        assertTrue(resp.isSuccessful)
    }

    @Test
    fun `patchUser happy path for image upload`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200).setBody("OK"))
        val dto = PatchUserDTO(userCode = "U1", images = listOf(byteArrayOf(1, 2, 3)))
        val resp = userApi.patchUser(dto)
        assertTrue(resp.isSuccessful)
    }
}