package com.vmobile.astronomypics.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "picture_for_day")
data class PlanetaryResponse(
    @PrimaryKey
    @SerializedName("date")
    var dateOfAPOD: String = "",
    @SerializedName("explanation")
    var explanation: String = "",
    @SerializedName("hdurl")
    var highDefinitionImageURL: String = "",
    @SerializedName("media_type")
    var mediaType: String = "",
    @SerializedName("title")
    var imageTitle: String = "",
    @SerializedName("url")
    var mediaURL: String = "",
    @SerializedName("copyright")
    var copyrightAuthor: String = "",
    var isFavorite: Boolean = false,
    var statusCode: Int = 201,
)