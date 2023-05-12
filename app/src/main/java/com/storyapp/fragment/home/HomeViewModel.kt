package com.storyapp.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.storyapp.model.UserModel
import com.storyapp.model.UserPreferences
import com.storyapp.remote.ApiService
import com.storyapp.remote.response.ResponseStory
import com.storyapp.remote.response.ResultStatus
import kotlinx.coroutines.launch

class HomeViewModel(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) : ViewModel() {

    fun getUser(): LiveData<UserModel> = userPreferences.getUser().asLiveData()

    fun fetchStories(): LiveData<ResultStatus<ResponseStory>> = liveData {

        if (userPreferences.isLogin().asLiveData().value == true) {
            emit(ResultStatus.Loading)
            try {
                val responseStory =
                    apiService.fetchStories(
                        userPreferences.getToken().asLiveData().value.toString()
                    )
                if (!responseStory.error) {
                    emit(ResultStatus.Success(responseStory))
                } else {
                    emit(ResultStatus.Error(responseStory.message))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emit(ResultStatus.Error(e.message.toString()))
            }
        }

    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.resetUser()
        }
    }
}