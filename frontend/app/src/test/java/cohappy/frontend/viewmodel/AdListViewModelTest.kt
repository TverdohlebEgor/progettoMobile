package cohappy.frontend.viewmodel

import cohappy.frontend.client.dto.response.GetHouseAdvertesimentDTO
import cohappy.frontend.repository.AdListRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdListViewModelTest {

    private lateinit var viewModel: AdListViewModel
    private val repository: AdListRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AdListViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadAdvertisements success updates adsList`() = runTest {
        val mockAds = listOf(
            GetHouseAdvertesimentDTO(houseCode = "H1", street = "Street 1", description = "Desc 1"),
            GetHouseAdvertesimentDTO(houseCode = "H2", street = "Street 2", description = "Desc 2")
        )
        coEvery { repository.fetchAds() } returns Result.success(mockAds)

        viewModel.loadAdvertisements()
        
        assertTrue(viewModel.isLoading)
        
        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertEquals(2, viewModel.adsList.size)
        assertEquals("H1", viewModel.adsList[0].houseCode)
    }

    @Test
    fun `loadAdvertisements retries on failure and eventually succeeds`() = runTest {
        val mockAds = listOf(GetHouseAdvertesimentDTO(houseCode = "H1"))
        coEvery { repository.fetchAds() } returnsMany listOf(
            Result.failure(Exception("500 Error")),
            Result.success(mockAds)
        )

        viewModel.loadAdvertisements()
        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertEquals(1, viewModel.adsList.size)
        coVerify(exactly = 2) { repository.fetchAds() }
    }

    @Test
    fun `loadAdvertisements retries up to maxRetries and remains empty on persistent failure`() = runTest {
        coEvery { repository.fetchAds() } returns Result.failure(Exception("Persistent Error"))

        viewModel.loadAdvertisements()
        advanceUntilIdle()

        assertFalse(viewModel.isLoading)
        assertTrue(viewModel.adsList.isEmpty())
        coVerify(exactly = 3) { repository.fetchAds() }
    }

    @Test
    fun `getFilteredAds returns filtered list based on search query`() = runTest {
        val mockAds = listOf(
            GetHouseAdvertesimentDTO(houseCode = "H1", street = "Main Street", description = "Beautiful house"),
            GetHouseAdvertesimentDTO(houseCode = "H2", street = "Second Ave", description = "Modern apartment")
        )
        coEvery { repository.fetchAds() } returns Result.success(mockAds)

        viewModel.loadAdvertisements()
        advanceUntilIdle()

        viewModel.updateSearchQuery("Main")
        val filtered = viewModel.getFilteredAds()

        assertEquals(1, filtered.size)
        assertEquals("H1", filtered[0].houseCode)
    }

    @Test
    fun `updateSearchQuery updates searchQuery state`() {
        viewModel.updateSearchQuery("test query")
        assertEquals("test query", viewModel.searchQuery)
    }
}
