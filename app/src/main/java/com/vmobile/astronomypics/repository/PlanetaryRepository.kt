package com.vmobile.astronomypics.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vmobile.astronomypics.database.PlanetaryDatabase
import com.vmobile.astronomypics.models.PlanetaryResponse
import com.vmobile.astronomypics.network.PlanetaryNetworkHelper
import com.vmobile.astronomypics.network.PlanetaryNetworkService
import com.vmobile.astronomypics.utils.DateUtils
import com.vmobile.astronomypics.utils.NetworkUtils
import org.jetbrains.annotations.TestOnly
import java.util.*

class PlanetaryRepository(context: Context) {

    private var mContext: Context = context

    private var planetaryNetworkService: PlanetaryNetworkService = PlanetaryNetworkHelper.getInstance().create(PlanetaryNetworkService::class.java)

    private val resultStatusLiveData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val pictureOfDayLiveData: MutableLiveData<PlanetaryResponse> = MutableLiveData<PlanetaryResponse>()
    private val pictureOfDayListLiveData: MutableLiveData<List<PlanetaryResponse>> = MutableLiveData<List<PlanetaryResponse>>()

    private val planetaryDatabase: PlanetaryDatabase = PlanetaryDatabase.getDatabase(context)

    val pictureOfDay: LiveData<PlanetaryResponse>
        get() = pictureOfDayLiveData

    val favoritePictureList: LiveData<List<PlanetaryResponse>>
        get() = pictureOfDayListLiveData

    val result: LiveData<Boolean>
        get() = resultStatusLiveData

    /**
     * Method will update LiveData for picture of the day
     *
     * @param apiKey [String]
     */
    suspend fun getAstronomyPictureOfDay(apiKey: String) {
        if (NetworkUtils.isNetworkAvailable(mContext)) {

            // Get the response from server
            val result = planetaryNetworkService.getAstronomyPictureOfDay(apiKey)
            if (result.isSuccessful) {
                result.body()?.let {
                    if (it.mediaType == "video") {
                        it.highDefinitionImageURL = ""
                    }
                    if (it.copyrightAuthor == null) {
                        it.copyrightAuthor = ""
                    }

                    // Check for DB entry for current day and merge the response from server
                    val resultDB = planetaryDatabase.astronomyPictureDAO().getAstronomyPictures(it.dateOfAPOD)
                    if (resultDB != null) {
                        it.isFavorite = resultDB.isFavorite
                    } else {
                        planetaryDatabase.astronomyPictureDAO().addAstronomy(it)
                    }
                    pictureOfDayLiveData.postValue(it)
                }
            } else {
                val errorBody = PlanetaryResponse(DateUtils.getCurrentDate(), statusCode = result.code())
                pictureOfDayLiveData.postValue(errorBody)
            }
        } else {

            // If network not available, get it from DB
            var result = planetaryDatabase.astronomyPictureDAO().getAstronomyPictures(DateUtils.getCurrentDate())
            if (result == null) {

                // No data available, set the status code to 503
                result = PlanetaryResponse(dateOfAPOD = DateUtils.getCurrentDate(), statusCode = 503)
            }
            pictureOfDayLiveData.postValue(result)
        }
    }

    /**
     * Method will return the model class for picture by date
     *
     * @param apiKey
     * @param date [String]
     */
    suspend fun getAstronomyPictureOfDayByDate(apiKey: String, date: String) {
        if (NetworkUtils.isNetworkAvailable(mContext)) {
            val result = planetaryNetworkService.getAstronomyPictureOfDayByDate(apiKey, date)
            if (result.isSuccessful) {
                result.body()?.let {
                    if (it.mediaType == "video") {
                        it.highDefinitionImageURL = ""
                    }
                    if (it.copyrightAuthor == null) {
                        it.copyrightAuthor = ""
                    }

                    // Check for DB entry for specific day and merge the response from server
                    val resultDB = planetaryDatabase.astronomyPictureDAO().getAstronomyPictures(it.dateOfAPOD)
                    if (resultDB != null) {
                        it.isFavorite = resultDB.isFavorite
                    } else {
                        planetaryDatabase.astronomyPictureDAO().addAstronomy(it)
                    }

                    // update the LiveData
                    pictureOfDayLiveData.postValue(it)
                }
            } else {
                val errorBody = PlanetaryResponse(DateUtils.getCurrentDate(), statusCode = result.code())
                pictureOfDayLiveData.postValue(errorBody)
            }
        } else {

            // If network not available, get it from DB
            var result = planetaryDatabase.astronomyPictureDAO().getAstronomyPictures(date)
            if (result == null) {

                // No data available, set the status code to 503
                result = PlanetaryResponse(dateOfAPOD = date, statusCode = 503)
            }
            pictureOfDayLiveData.postValue(result)
        }
    }

    /**
     * Add the picture as favorite or remove favorite picture in DB
     *
     * @param planetaryResponse
     */
    suspend fun updateFavouritePicture(planetaryResponse: PlanetaryResponse) {
        planetaryDatabase.astronomyPictureDAO().updateAstronomy(planetaryResponse)
    }

    /**
     * Update the list of pics in DB
     *
     * @param planetaryResponseList
     */
    suspend fun updateFavouritePictureList(planetaryResponseList: List<PlanetaryResponse>) {
        for (item in planetaryResponseList) {
            planetaryDatabase.astronomyPictureDAO().updateAstronomy(item)
        }
        resultStatusLiveData.postValue(true)
    }

    /**
     * Get the list of favorite pics from DB
     *
     */
    suspend fun getFavoritePictures() {
        val result = planetaryDatabase.astronomyPictureDAO().getFavoriteAstronomyPictures()
        pictureOfDayListLiveData.postValue(result)
    }

    /**
     * Update the network service for testing purpose when web-server is mocked
     *
     * @param service [PlanetaryNetworkService]
     */
    @TestOnly
    fun updateNetworkService(service: PlanetaryNetworkService) {
        planetaryNetworkService = service
    }
}