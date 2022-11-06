package com.vmobile.astronomypics.viewModel

import android.app.Application
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.vmobile.astronomypics.models.PlanetaryResponse
import com.vmobile.astronomypics.repository.PlanetaryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavouriteViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: PlanetaryRepository = PlanetaryRepository(application.applicationContext)

    // Default dispatchers for API calls
    private var dispatcher: CoroutineDispatcher = Dispatchers.IO

    val favoritePictureList: LiveData<List<PlanetaryResponse>>
        get() = repository.favoritePictureList

    val resultStatus: LiveData<Boolean>
        get() = repository.result

    /**
     * Get the current picture of day from DB
     *
     */
    fun getFavoritePics() {
        viewModelScope.launch(dispatcher) {
            repository.getFavoritePictures()
        }
    }

    /**
     * Update the list of pictures in DB whether it is favorite or not
     *
     * @param planetaryResponseList
     */
    fun updateFavouritePic(planetaryResponseList: List<PlanetaryResponse>) {
        viewModelScope.launch(dispatcher) {
            repository.updateFavouritePictureList(planetaryResponseList)
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