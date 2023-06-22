package com.story.fragment.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.story.data.StoryRepository
import com.story.model.UserPreferences
import com.story.remote.ApiService
import com.story.remote.response.ResponseStory
import com.story.utils.ResultStatus
import com.story.utils.showValidToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StoryMapsViewModel(
    private val apiService: ApiService,
    private val storyRepository: StoryRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _storyMap: MutableLiveData<ResultStatus<ResponseStory>> = MutableLiveData()
    val storyMap: LiveData<ResultStatus<ResponseStory>> get() = _storyMap
    private val location = 1

    init {
        fetchStoriesWithLocation()
    }

    fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            _storyMap.postValue(ResultStatus.Loading)
            try {
                val token = showValidToken(userPreferences.getToken().first())
                val storyMapResponse = apiService.fetchStories(token, location)
                _storyMap.postValue(ResultStatus.Success(storyMapResponse))
            } catch (e: Exception) {
                e.printStackTrace()
                _storyMap.postValue(ResultStatus.Error(e.message.toString()))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.resetUser()
            storyRepository.clearStory()
        }
    }
}