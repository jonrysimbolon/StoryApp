package com.storyapp.fragment.add

import androidx.lifecycle.ViewModel
import com.storyapp.remote.ApiService
import com.storyapp.model.UserPreferences

class AddStoryViewModel(
    private val apiService: ApiService,
    private val pref: UserPreferences
) : ViewModel() {

}