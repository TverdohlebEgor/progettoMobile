package cohappy.frontend.repository

import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.HouseApiClient
import cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO
import cohappy.frontend.expections.ErrorMessages.SERVER_ERROR
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

class AdListRepositoryTest {

    private val houseApi = mockk<HouseApiClient>()
    private lateinit var repository: AdListRepository

    @Before
    fun setup() {
        mockkObject(ClientSingleton)
        every { ClientSingleton.houseApi } returns houseApi
        repository = AdListRepository()
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `fetchAds success returns list of advertisements`() = runTest {
        val mockAds = listOf(GetHouseAdvertesimentDTO(houseCode = "HOUSE1", description = "Nice house"))
        coEvery { houseApi.getAllHouseAdvertisements() } returns Response.success(mockAds)

        val result = repository.fetchAds()

        assertTrue(result.isSuccess)
        assertEquals(mockAds, result.getOrNull())
    }

    @Test
    fun `fetchAds failure returns ServerErrorException`() = runTest {
        coEvery { houseApi.getAllHouseAdvertisements() } returns Response.error(500, "".toResponseBody())

        val result = repository.fetchAds()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ServerErrorException)
        assertEquals(SERVER_ERROR, result.exceptionOrNull()?.message)
    }

    @Test
    fun `fetchAds success with null body returns ServerErrorException`() = runTest {
        coEvery { houseApi.getAllHouseAdvertisements() } returns Response.success(null)

        val result = repository.fetchAds()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ServerErrorException)
        assertEquals(SERVER_ERROR, result.exceptionOrNull()?.message)
    }

    @Test
    fun `fetchAds exception returns failure`() = runTest {
        val exception = Exception("Network Error")
        coEvery { houseApi.getAllHouseAdvertisements() } throws exception

        val result = repository.fetchAds()

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
