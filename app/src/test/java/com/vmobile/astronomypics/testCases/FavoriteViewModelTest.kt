package com.vmobile.astronomypics.testCases

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.vmobile.astronomypics.network.PlanetaryNetworkService
import com.vmobile.astronomypics.repository.PlanetaryRepository
import com.vmobile.astronomypics.utils.CoroutineTestRule
import com.vmobile.astronomypics.viewModel.FavouriteViewModel
import io.mockk.clearAllMocks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.N])
@RunWith(RobolectricTestRunner::class)
class FavoriteViewModelTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var favouriteViewModel: FavouriteViewModel
    private lateinit var apiService: PlanetaryNetworkService
    private lateinit var gson: Gson
    private lateinit var mResources: Resources
    private lateinit var activity: AppCompatActivity

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    var coroutineTestRule = CoroutineTestRule()

    private lateinit var planetaryRepository: PlanetaryRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(coroutineTestRule.testDispatcher)
        clearAllMocks()

        val activityController = Robolectric.buildActivity(AppCompatActivity::class.java)
        activity = activityController.get() as AppCompatActivity

        planetaryRepository = PlanetaryRepository(activity)
        mResources = activity.resources
        favouriteViewModel = FavouriteViewModel(activity.application)
        favouriteViewModel.updateDispatcher(coroutineTestRule.testDispatcher)
    }

    @Test
    fun `test list of favorite astronomy picture`() {
        runTest {
            favouriteViewModel.getFavoritePics()
            Assert.assertNotNull(favouriteViewModel.favoritePictureList.value)
        }
    }

    @ExperimentalCoroutinesApi
    @After
    fun teardown() {
        mockWebServer.shutdown()
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}