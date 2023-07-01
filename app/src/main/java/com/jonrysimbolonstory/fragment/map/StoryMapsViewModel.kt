package com.jonrysimbolonstory.fragment.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jonrysimbolonstory.data.StoryRepository
import com.jonrysimbolonstory.model.UserPreferences
import com.jonrysimbolonstory.remote.ApiService
import com.jonrysimbolonstory.remote.response.ResponseStory
import com.jonrysimbolonstory.utils.ResultStatus
import com.jonrysimbolonstory.utils.showValidToken
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