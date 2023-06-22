package com.story.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "story")
data class StoryModel(
    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("description")
    val description: String,

    @PrimaryKey
    @SerializedName("id")
    val id: String,

    @SerializedName("lat")
    val lat: Double,

    @SerializedName("lon")
    val lon: Double,

    @SerializedName("name")
    val name: String,

    @SerializedName("photoUrl")
    val photoUrl: String
)