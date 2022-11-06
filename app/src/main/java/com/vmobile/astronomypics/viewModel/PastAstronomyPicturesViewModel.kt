package com.vmobile.astronomypics.viewModel

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.vmobile.astronomypics.models.PlanetaryResponse
import com.vmobile.astronomypics.repository.PlanetaryRepository
import com.vmobile.astronomypics.utils.GenericConstants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PastAstronomyPicturesViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: PlanetaryRepository = PlanetaryRepository(application.applicationContext)

    // Default dispatchers for API calls
    private var dispatcher: CoroutineDispatcher = Dispatchers.IO

    val pictureOfDay: LiveData<PlanetaryResponse>
        get() = repository.pictureOfDay

    /**
     * Get the picture for a specific date from server
     *
     * @param date [String] date in format yyyy-mm-dd
     */
    fun getPictureOfDay(date: String) {
        viewModelScope.launch(dispatcher) {
            repository.getAstronomyPictureOfDayByDate(GenericConstants.AUTH_KEY, date)
        }
    }

    /**
     * Add the picture as favorite or remove picture as favorite in DB
     *
     * @param planetaryResponse
     */
    fun updateFavouritePic(planetaryResponse: PlanetaryResponse) {
        viewModelScope.launch(dispatcher) {
            repository.updateFavouritePicture(planetaryResponse)
        }
    }

    /**
     * Update the CoroutineDispatcher for unit test
     *
     * @param newDispatcher [CoroutineDispatcher]
     */
    @VisibleForTesting
    fun updateDispatcher(newDispatcher: CoroutineDispatcher) {
        dispatcher = newDispatcher
    }
}