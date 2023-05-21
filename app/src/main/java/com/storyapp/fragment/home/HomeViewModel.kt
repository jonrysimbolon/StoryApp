package com.storyapp.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.storyapp.model.ErrorDialogModel
import com.storyapp.model.UserPreferences
import com.storyapp.remote.ApiService
import com.storyapp.remote.response.ResponseStory
import com.storyapp.remote.response.ResultStatus
import com.storyapp.utils.responseGsonPattern
import com.storyapp.utils.showValidToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences,
    private val gson: Gson
) : ViewModel() {

    private var _failure = MutableLiveData<ErrorDialogModel>()
    val failure get() = _failure


    fun showFailureImage(show: Boolean, desc: String) {
        _failure.value = ErrorDialogModel(
            show,
            desc
        )
    }

    fun fetchStories(): LiveData<ResultStatus<ResponseStory>> = liveData {
        val isLoggedIn = userPreferences.isLogin().first()
        if (isLoggedIn) {
            emit(ResultStatus.Loading)
            try {
                val token = showValidToken(userPreferences.getToken().first())
                val responseStory = apiService.fetchStories(token)
                val responseBody = responseStory.body()
                if (responseStory.isSuccessful && responseBody != null) {
                    emit(ResultStatus.Success(responseBody))
                } else {
                    emit(
                        ResultStatus.Error(
                            responseGsonPattern(
                                gson,
                                responseStory
                                    .errorBody()
                                    ?.string()
                                    .toString()
                            ).message
                        )
                    )
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