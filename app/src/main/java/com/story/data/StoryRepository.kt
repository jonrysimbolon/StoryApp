@file:OptIn(ExperimentalPagingApi::class)

package com.story.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.story.local.StoryDatabase
import com.story.model.StoryModel


class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val storyRemoteMediator: StoryRemoteMediator
) {
    fun getStory(): LiveData<PagingData<StoryModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = storyRemoteMediator,
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllStory()
            },
        ).liveData
    }

    suspend fun clearStory() {
        storyDatabase.remoteKeysDao().deleteRemoteKeys()
        storyDatabase.storyDao().deleteAll()
    }
}