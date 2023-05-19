package com.storyapp.model

import androidx.lifecycle.LiveData
import com.storyapp.remote.response.ImageState

data class ItemImage(
    val imageUrl: String,
    val imageState: LiveData<ImageState>
)