package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.HouseApiClient
import cohappy.frontend.client.dto.enum.HouseStateEnum
import cohappy.frontend.client.dto.request.CreateHouseAdvertisementDTO
import cohappy.frontend.expections.ErrorMessages.CREATE_HOME_BAD_REQUEST
import cohappy.frontend.expections.ErrorMessages.SERVER_ERROR
import cohappy.frontend.expections.ErrorMessages.USER_NOT_FOUND_CREATE_HOUSE
import cohappy.frontend.expections.ServerErrorException
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class CreateAdRepositoryTest {

    private val houseApi = mockk<HouseApiClient>()
    private lateinit var repository: CreateAdRepository

    @Before
    fun setup() {
        mockkObject(ClientSingleton)
        every { ClientSingleton.houseApi } returns houseApi
        repository = CreateAdRepository()
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `createAdvertisement success returns result string`() = runTest {
        val dto = CreateHouseAdvertisementDTO(
            houseCode = "HOUSE123",
            state = HouseStateEnum.PUBLIC,
            publishedBy = "USER123",
            description = "Bella casa"
        )
        val successMsg = "Created"
        coEvery { houseApi.createHouseAdvertisement(dto) } returns Response.success(successMsg)

        val result = repository.createAdvertisement(dto)

        assertTrue(result.isSuccess)
        assertEquals(successMsg, result.getOrNull())
    }

    @Test
    fun `createAdvertisement 400 returns CREATE_HOME_BAD_REQUEST failure`() = runTest {
        val dto = CreateHouseAdvertisementDTO("H1", null, HouseStateEnum.PUBLIC, "U1")
        coEvery { houseApi.createHouseAdvertisement(dto) } returns Response.error(400, "".toResponseBody())

        val result = repository.createAdvertisement(dto)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ServerErrorException)
        assertEquals(CREATE_HOME_BAD_REQUEST, result.exceptionOrNull()?.message)
    }

    @Test
    fun `createAdvertisement 404 returns USER_NOT_FOUND_CREATE_HOUSE failure`() = runTest {
        val dto = CreateHouseAdvertisementDTO("H1", null, HouseStateEnum.PUBLIC, "U1")
        coEvery { houseApi.createHouseAdvertisement(dto) } returns Response.error(404, "".toResponseBody())

        val result = repository.createAdvertisement(dto)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ServerErrorException)
        assertEquals(USER_NOT_FOUND_CREATE_HOUSE, result.exceptionOrNull()?.message)
    }

    @Test
    fun `createAdvertisement 500 returns SERVER_ERROR failure`() = runTest {
        val dto = CreateHouseAdvertisementDTO("H1", null, HouseStateEnum.PUBLIC, "U1")
        coEvery { houseApi.createHouseAdvertisement(dto) } returns Response.error(500, "".toResponseBody())

        val result = repository.createAdvertisement(dto)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ServerErrorException)
        assertEquals(SERVER_ERROR, result.exceptionOrNull()?.message)
    }

    @Test
    fun `createAdvertisement exception returns failure`() = runTest {
        val dto = CreateHouseAdvertisementDTO("H1", null, HouseStateEnum.PUBLIC, "U1")
        val exception = Exception("Network fail")
        coEvery { houseApi.createHouseAdvertisement(dto) } throws exception

        val result = repository.createAdvertisement(dto)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
