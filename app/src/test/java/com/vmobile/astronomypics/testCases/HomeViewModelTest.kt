package com.vmobile.astronomypics.testCases

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.res.Resources
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.vmobile.astronomypics.R
import com.vmobile.astronomypics.network.PlanetaryNetworkService
import com.vmobile.astronomypics.repository.PlanetaryRepository
import com.vmobile.astronomypics.utils.CoroutineTestRule
import com.vmobile.astronomypics.viewModel.HomeViewModel
import io.mockk.clearAllMocks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.N])
@RunWith(RobolectricTestRunner::class)
class HomeViewModelTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var homeViewModel: HomeViewModel
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
        homeViewModel = HomeViewModel(activity.application)
        homeViewModel.updateDispatcher(coroutineTestRule.testDispatcher)

        gson = Gson()
        mockWebServer = MockWebServer().apply {
            start(8080)
        }
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/").toString())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(PlanetaryNetworkService::class.java)

        addResponse(mockWebServer, mResources.openRawResource(R.raw.result)
            .bufferedReader().use { it.readText() })
        planetaryRepository.updateNetworkService(apiService)
    }

    @Test
    fun `test current day astronomy picture`() {
        runTest {
            homeViewModel.getPictureOfDay()
            Assert.assertEquals(homeViewModel.pictureOfDay.value?.dateOfAPOD, "2022-11-06")
        }
    }

    private fun addResponse(server: MockWebServer, json: String, statusCode: Int = 200) {
        with(MockResponse()) {
            setBody(json)
            setResponseCode(statusCode)
            server.enqueue(this)
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
