package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.HouseApiClient
import cohappy.frontend.client.dto.enum.HouseStateEnum
import cohappy.frontend.client.dto.request.CreateHouseAdvertisementDTO
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

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
    fun `createAdvertisement happy path returns success`() = runTest {
        val dto = CreateHouseAdvertisementDTO(
            houseCode = "H1",
            state = HouseStateEnum.PUBLIC,
            publishedBy = "U1",
            description = "Desc",
            images = null
        )
        coEvery { houseApi.createHouseAdvertisement(dto) } returns Response.success("AD_99")

        val result = repository.createAdvertisement(dto)

        assertTrue(result.isSuccessful)
        assertEquals("AD_99", result.body())
        coVerify(exactly = 1) { houseApi.createHouseAdvertisement(dto) }
    }

    @Test
    fun `createAdvertisement unhappy path 400 returns error response`() = runTest {
        val dto = CreateHouseAdvertisementDTO(
            houseCode = "H1",
            state = HouseStateEnum.PUBLIC,
            publishedBy = "U1",
            description = "Desc",
            images = null
        )
        coEvery { houseApi.createHouseAdvertisement(dto) } returns Response.error(400, "".toResponseBody())

        val result = repository.createAdvertisement(dto)

        assertFalse(result.isSuccessful)
        assertEquals(400, result.code())
    }

    @Test
    fun `createAdvertisement network exception throws`() = runTest {
        val dto = CreateHouseAdvertisementDTO(
            houseCode = "H1",
            state = HouseStateEnum.PUBLIC,
            publishedBy = "U1",
            description = "Desc",
            images = null
        )
        coEvery { houseApi.createHouseAdvertisement(dto) } throws IOException("No internet")

        try {
            repository.createAdvertisement(dto)
        } catch (e: Exception) {
            assertTrue(e is IOException)
        }
    }
}