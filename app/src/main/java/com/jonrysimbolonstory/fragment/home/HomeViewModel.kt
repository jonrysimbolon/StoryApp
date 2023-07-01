package com.jonrysimbolonstory.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jonrysimbolonstory.data.StoryRepository
import com.jonrysimbolonstory.model.StoryModel
import com.jonrysimbolonstory.model.UserPreferences
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class HomeViewModel(
    private val storyRepository: StoryRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    private val _story: MutableLiveData<PagingData<StoryModel>> = MutableLiveData()
    val story: LiveData<PagingData<StoryModel>> get() = _story

    private var storyJob: Job? = null

    fun observeLoginState() {
        userPreferences.isLogin()
            .distinctUntilChanged()
            .onEach { isLogin ->
                if (isLogin) {
                    startStoryJob()
                } else {
                    stopStoryJob()
                    _story.value = PagingData.empty()
                }
            }
            .launchIn(viewModelScope)
    }

    private fun startStoryJob() {
        if (storyJob?.isActive != true) {
            stopStoryJob()
            storyJob = viewModelScope.launch {
                val storyData = storyRepository.getStory().cachedIn(viewModelScope)
                storyData.observeForever { data ->
                    _story.value = data ?: PagingData.empty()
                }
            }
        }
    }

    private fun stopStoryJob() {
        storyJob?.cancel()
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.resetUser()
            storyRepository.clearStory()
        }
    }
}