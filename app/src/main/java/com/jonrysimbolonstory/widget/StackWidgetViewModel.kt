package com.jonrysimbolonstory.widget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jonrysimbolonstory.model.StoryModel
import com.jonrysimbolonstory.model.UserPreferences
import com.jonrysimbolonstory.remote.ApiService
import com.jonrysimbolonstory.utils.showValidToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StackWidgetViewModel(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences,
) : ViewModel() {
    private val _widgetItems = MutableLiveData<List<StoryModel>>()
    val widgetItems: LiveData<List<StoryModel>>
        get() = _widgetItems

    fun loadWidgetItem() {
        viewModelScope.launch {
            val isLoggedIn = userPreferences.isLogin().first()
            if (isLoggedIn) {
                try {
                    val token = showValidToken(userPreferences.getToken().first())
                    val responseStory = apiService.fetchStories(token)
                    val responseBody = responseStory.listStory
                    val listStoryModel = mutableListOf<StoryModel>()
                    for (story in responseBody) {
                        listStoryModel.add(
                            StoryModel(
                                createdAt = story.createdAt,
                                description = story.description,
                                id = story.id,
                                lat = story.lat,
                                lon = story.lon,
                                name = story.name,
                                photoUrl = story.photoUrl
                            )
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _widgetItems.postValue(emptyList())
                }
            } else {
                _widgetItems.postValue(emptyList())
            }
        }
    }
}