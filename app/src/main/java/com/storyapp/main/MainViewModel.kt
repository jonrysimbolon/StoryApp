package com.storyapp.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.storyapp.model.UserPreferences

class MainViewModel(
    private val pref: UserPreferences
): ViewModel() {

    private var _loading = MutableLiveData(false)
    val loading get() = _loading

    fun showLoading(show: Boolean){
        _loading.value = show
    }

    fun isLogin(): LiveData<Boolean> = pref.isLogin().asLiveData()
}