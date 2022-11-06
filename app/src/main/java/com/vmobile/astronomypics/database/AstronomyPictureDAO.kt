package com.vmobile.astronomypics.database

import androidx.room.*
import com.vmobile.astronomypics.models.PlanetaryResponse

@Dao
interface AstronomyPictureDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAstronomy(list: PlanetaryResponse)

    @Query("SELECT * FROM picture_for_day where dateOfAPOD = :date")
    suspend fun getAstronomyPictures(date: String): PlanetaryResponse

    @Query("SELECT * FROM picture_for_day where isFavorite=1")
    suspend fun getFavoriteAstronomyPictures(): List<PlanetaryResponse>

    @Update
    suspend fun updateAstronomy(list: PlanetaryResponse)
}