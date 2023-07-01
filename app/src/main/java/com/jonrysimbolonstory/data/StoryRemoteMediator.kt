@file:OptIn(ExperimentalPagingApi::class)

package com.jonrysimbolonstory.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.jonrysimbolonstory.local.StoryDatabase
import com.jonrysimbolonstory.model.RemoteKeys
import com.jonrysimbolonstory.model.StoryModel
import com.jonrysimbolonstory.model.UserPreferences
import com.jonrysimbolonstory.remote.ApiService
import com.jonrysimbolonstory.utils.showValidToken
import com.jonrysimbolonstory.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.first

class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) : RemoteMediator<Int, StoryModel>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryModel>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        return try {
            wrapEspressoIdlingResource {
                val token = showValidToken(userPreferences.getToken().first())
                val responseData =
                    apiService.fetchStories(token, null, page, state.config.pageSize).listStory
                val endOfPaginationReached = responseData.isEmpty()
                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        database.remoteKeysDao().deleteRemoteKeys()
                        database.storyDao().deleteAll()
                    }
                    val prevKey = if (page == 1) null else page - 1
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val keys = responseData.map {
                        RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                    }
                    database.remoteKeysDao().insertAll(keys)
                    database.storyDao().insertStory(responseData)
                }
                MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            }
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    override suspend fun initialize(): InitializeAction = InitializeAction.LAUNCH_INITIAL_REFRESH

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryModel>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryModel>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryModel>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}