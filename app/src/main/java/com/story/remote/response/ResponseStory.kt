package com.story.remote.response

import com.google.gson.annotations.SerializedName
import com.story.model.StoryModel

data class ResponseStory(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("listStory")
    val listStory: List<StoryModel>,
    @SerializedName("message")
    val message: String
)