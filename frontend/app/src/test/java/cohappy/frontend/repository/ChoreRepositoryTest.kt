package cohappy.frontend.repository

import cohappy.frontend.client.ChoreApiClient
import cohappy.frontend.client.ClientSingleton
import cohappy.frontend.client.dto.request.CreateChoreDTO
import cohappy.frontend.client.dto.request.PatchChoreDTO
import cohappy.frontend.client.dto.response.GetChoreDTO
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
import java.time.LocalDate

class ChoreRepositoryTest {

    private val choreApi = mockk<ChoreApiClient>()
    private lateinit var repository: ChoreRepository

    @Before
    fun setup() {
        mockkObject(ClientSingleton)
        every { ClientSingleton.choreApi } returns choreApi
        repository = ChoreRepository()
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `fetchChores happy path returns success`() = runTest {
        val houseCode = "HOUSE_123"
        val date = LocalDate.now()
        val expectedList = listOf(GetChoreDTO(name = "Bagno"))

        coEvery { choreApi.getChore(eq(houseCode), any<LocalDate>()) } returns Response.success(expectedList)

        val result = repository.fetchChores(houseCode, date)

        assertTrue(result.isSuccessful)
        assertEquals(expectedList, result.body())
        coVerify(exactly = 1) { choreApi.getChore(eq(houseCode), any<LocalDate>()) }
    }

    @Test
    fun `fetchChores unhappy path 500`() = runTest {
        val houseCode = "HOUSE_123"
        val date = LocalDate.now()

        coEvery { choreApi.getChore(eq(houseCode), any<LocalDate>()) } returns Response.error(500, "".toResponseBody())

        val result = repository.fetchChores(houseCode, date)

        assertFalse(result.isSuccessful)
        assertEquals(500, result.code())
    }

    @Test
    fun `updateChoreStatus happy path returns success`() = runTest {
        val patchDto = PatchChoreDTO(choreCode = "C1", completed = true)

        coEvery { choreApi.patchChore(patchDto) } returns Response.success("OK")

        val result = repository.updateChoreStatus(patchDto)

        assertTrue(result.isSuccessful)
        assertEquals("OK", result.body())
    }

    @Test
    fun `updateChoreStatus unhappy path 404`() = runTest {
        val patchDto = PatchChoreDTO(choreCode = "INVALID", completed = true)

        coEvery { choreApi.patchChore(patchDto) } returns Response.error(404, "".toResponseBody())

        val result = repository.updateChoreStatus(patchDto)

        assertFalse(result.isSuccessful)
        assertEquals(404, result.code())
    }

    @Test
    fun `createChore happy path returns success`() = runTest {
        val createDto = CreateChoreDTO(
            name = "Cucina",
            description = "Desc",
            createdBy = "U1",
            houseCode = "H1",
            days = listOf(LocalDate.now()),
            assignedTo = mapOf(LocalDate.now() to "U2")
        )

        coEvery { choreApi.createChore(createDto) } returns Response.success("NEW_ID")

        val result = repository.createChore(createDto)

        assertTrue(result.isSuccessful)
        assertEquals("NEW_ID", result.body())
    }

    @Test
    fun `createChore unhappy path 400`() = runTest {
        val createDto = CreateChoreDTO(
            name = "Cucina",
            description = "Desc",
            createdBy = "U1",
            houseCode = "H1",
            days = listOf(LocalDate.now()),
            assignedTo = mapOf(LocalDate.now() to "U2")
        )

        coEvery { choreApi.createChore(createDto) } returns Response.error(400, "".toResponseBody())

        val result = repository.createChore(createDto)

        assertFalse(result.isSuccessful)
        assertEquals(400, result.code())
    }
}