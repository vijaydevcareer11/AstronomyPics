package com.vmobile.astronomypics.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vmobile.astronomypics.models.PlanetaryResponse

@Database(entities = [PlanetaryResponse::class], version = 1, exportSchema = false)
abstract class PlanetaryDatabase : RoomDatabase() {

    abstract fun astronomyPictureDAO(): AstronomyPictureDAO

    companion object {

        @Volatile
        private var INSTANCE: PlanetaryDatabase? = null

        fun getDatabase(context: Context): PlanetaryDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        PlanetaryDatabase::class.java,
                        "planetaryDB"
                    )
                        .build()
                }
            }
            return INSTANCE!!
        }
    }
}