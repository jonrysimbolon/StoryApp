package com.storyapp.fragment.detail

import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModel
import com.bumptech.glide.RequestManager

class DetailStoryViewModel(
    private val glide: RequestManager
) : ViewModel() {

    fun setImageToView(url: String, imageView: AppCompatImageView) {
        glide.load(url)
            .into(imageView)
    }

}