package com.vmobile.astronomypics.testCases.database

import android.app.Activity
import android.os.Build
import com.vmobile.astronomypics.database.PlanetaryDatabase
import com.vmobile.astronomypics.models.PlanetaryResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.N])
@RunWith(RobolectricTestRunner::class)
class PlanetaryDataBaseTest {

    private lateinit var planetaryDatabase: PlanetaryDatabase

    @Before
    fun setup() {
        val activityController = Robolectric.buildActivity(Activity::class.java)
        val activity = activityController.get()

        planetaryDatabase = PlanetaryDatabase.getDatabase(activity)
    }

    /**
     * Test whether data inserted in DB or not
     *
     */
    @Test
    fun `test inserted data`() {
        val response = PlanetaryResponse().run {
            dateOfAPOD = "2022-11-06"
            explanation = "Does this strange dark ball look somehow familiar?"
            isFavorite = false
            mediaType = "image"
            imageTitle = "Dark Ball in Inverted Starfield"
            this
        }

        runBlocking {
            planetaryDatabase.astronomyPictureDAO().addAstronomy(response)
            val data = planetaryDatabase.astronomyPictureDAO().getAstronomyPictures("2022-11-06")
            Assert.assertEquals(data.imageTitle, response.imageTitle)
        }
    }

    /**
     * Test for favorite item in DB
     *
     */
    @Test
    fun `test for favorite picture`() {
        val response = PlanetaryResponse().run {
            dateOfAPOD = "2022-11-03"
            explanation = "The small, northern constellation Triangulum harbors"
            isFavorite = true
            mediaType = "image"
            imageTitle = "M33: The Triangulum Galaxy"
            this
        }

        runBlocking {
            planetaryDatabase.astronomyPictureDAO().addAstronomy(response)
            val data = planetaryDatabase.astronomyPictureDAO().getFavoriteAstronomyPictures()
            Assert.assertEquals(data[0].isFavorite, response.isFavorite)
        }
    }
}