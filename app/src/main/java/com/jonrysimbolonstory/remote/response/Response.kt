package com.jonrysimbolonstory.remote.response

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)