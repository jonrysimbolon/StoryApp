package com.storyapp.fragment.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.storyapp.model.UserPreferences
import com.storyapp.remote.ApiService
import com.storyapp.remote.response.Response
import com.storyapp.remote.response.ResultStatus
import com.storyapp.utils.responseGsonPattern
import com.storyapp.utils.showValidToken
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences,
    private val gson: Gson
) : ViewModel() {

    fun addStory(file: MultipartBody.Part, description: RequestBody): LiveData<ResultStatus<Response>> = liveData {
        emit(ResultStatus.Loading)
        try {
            val token = showValidToken(userPreferences.getToken().first())
            val addStoryResponse = apiService.sendStory(token, file, description)
            val addStoryBody = addStoryResponse.body()
            if (addStoryResponse.isSuccessful && addStoryBody != null) {
                emit(ResultStatus.Success(addStoryBody))
            } else {
                emit(
                    ResultStatus.Error(
                        responseGsonPattern(
                            gson,
                            addStoryResponse
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