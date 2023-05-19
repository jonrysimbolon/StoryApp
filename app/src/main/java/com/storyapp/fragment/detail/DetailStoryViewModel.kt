package com.storyapp.fragment.detail

import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModel
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.storyapp.model.UserPreferences
import com.storyapp.remote.ApiService

class DetailStoryViewModel(
    private val apiService: ApiService,
    private val pref: UserPreferences,
    private val gson: Gson,
    private val glide: RequestManager
) : ViewModel() {

    fun setImageToView(url: String, imageView: AppCompatImageView){
        glide.load(url)
            .into(imageView)
    }

}