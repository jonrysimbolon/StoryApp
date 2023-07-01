package com.jonrysimbolonstory.remote.response

import com.google.gson.annotations.SerializedName
import com.jonrysimbolonstory.model.StoryModel

data class ResponseStory(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("listStory")
    val listStory: List<StoryModel>,
    @SerializedName("message")
    val message: String
)