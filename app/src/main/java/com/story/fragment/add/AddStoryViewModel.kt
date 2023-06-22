package com.story.fragment.add

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.story.model.UserPreferences
import com.story.remote.ApiService
import com.story.remote.response.Response
import com.story.utils.Event
import com.story.utils.ImageUtils
import com.story.utils.ResultStatus
import com.story.utils.responseGsonPattern
import com.story.utils.showValidToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class AddStoryViewModel(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences,
    private val gson: Gson,
    private val imageUtils: ImageUtils,
) : ViewModel() {

    private var _checkBoxState = MutableLiveData(false)
    val checkBoxState get() = _checkBoxState

    private var _buttonState = MutableLiveData(true)
    val buttonState get() = _buttonState

    private var _addStoryLiveData: MutableLiveData<Event<ResultStatus<Response>>> =
        MutableLiveData()
    val addStoryLiveData: LiveData<Event<ResultStatus<Response>>> get() = _addStoryLiveData

    fun addStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double? = null,
        lon: Double? = null
    ) {
        viewModelScope.launch {
            try {
                _addStoryLiveData.postValue(Event(ResultStatus.Loading))
                val token = showValidToken(userPreferences.getToken().first())
                val addStoryResponse = apiService.sendStory(token, file, description, lat, lon)
                val addStoryBody = addStoryResponse.body()
                if (addStoryResponse.isSuccessful && addStoryBody != null) {
                    _addStoryLiveData.postValue(Event(ResultStatus.Success(addStoryBody)))
                } else {
                    _addStoryLiveData.postValue(
                        Event(
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
                    )
                }
            } catch (e: Exception) {
                _addStoryLiveData.postValue(Event(ResultStatus.Error(e.message.toString())))
            }
        }
    }

    fun enableCheckbox(enable: Boolean) {
        _checkBoxState.value = enable
    }

    fun enableButton(enable: Boolean) {
        _buttonState.value = enable
    }

    fun removeObservers(owner: LifecycleOwner) {
        _addStoryLiveData.removeObservers(owner)
    }

    fun uriToFile(selectedImg: Uri, context: Context) = imageUtils.uriToFile(selectedImg, context)

    suspend fun processSelectedImage(file: File?): MultipartBody.Part =
        imageUtils.imageMultipart(file)
}