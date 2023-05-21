package com.storyapp.widget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.storyapp.model.StoryModel
import com.storyapp.model.UserPreferences
import com.storyapp.remote.ApiService
import com.storyapp.utils.showValidToken
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
                    val responseBody = responseStory.body()
                    if (responseStory.isSuccessful && responseBody != null) {
                        val listStory = responseBody.listStory
                        val listStoryModel = mutableListOf<StoryModel>()
                        for (story in listStory) {
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
                        _widgetItems.postValue(listStoryModel)
                    } else {
                        _widgetItems.postValue(emptyList())
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