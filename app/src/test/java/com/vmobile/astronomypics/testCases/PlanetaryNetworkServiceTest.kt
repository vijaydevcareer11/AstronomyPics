package com.vmobile.astronomypics.testCases

import android.app.Activity
import android.content.res.Resources
import android.os.Build
import com.google.gson.Gson
import com.vmobile.astronomypics.R
import com.vmobile.astronomypics.network.PlanetaryNetworkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
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
class PlanetaryNetworkServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: PlanetaryNetworkService
    private lateinit var gson: Gson
    private lateinit var mResources: Resources

    @Before
    fun setup() {
        val activityController = Robolectric.buildActivity(Activity::class.java)
        val activity = activityController.get()
        mResources = activity.resources

        MockitoAnnotations.initMocks(this)
        gson = Gson()
        mockWebServer = MockWebServer().apply {
            start(8080)
        }
        val url = mockWebServer.url("/").toString()
        apiService = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(PlanetaryNetworkService::class.java)
    }

    @Test
    fun getCurrentPictureOfDay() {
        addResponse(mockWebServer, mResources.openRawResource(R.raw.result)
            .bufferedReader().use { it.readText() })

        runBlocking {
            val response = apiService.getAstronomyPictureOfDay("")
            response.body()?.let {
                Assert.assertEquals(it.dateOfAPOD, "2022-11-06")
            }
        }
    }

    private fun addResponse(server: MockWebServer, json: String, statusCode: Int = 200) {
        with(MockResponse()) {
            setBody(json)
            setResponseCode(statusCode)
            server.enqueue(this)
        }
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
        Dispatchers.resetMain()
    }
}