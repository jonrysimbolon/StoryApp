package com.story.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.story.local.StoryDatabase
import com.story.model.StoryModel
import com.story.model.UserLoginModel
import com.story.model.UserPreferences
import com.story.model.UserRegistrationModel
import com.story.remote.ApiService
import com.story.remote.response.Response
import com.story.remote.response.ResponseLogin
import com.story.remote.response.ResponseStory
import junit.framework.TestCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(AndroidJUnit4::class)
class StoryRemoteMediatorTest {

    private val testContext: Context =
        ApplicationProvider.getApplicationContext()
    private val testDataStore: DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            produceFile =
            { testContext.preferencesDataStoreFile(PREFERENCES_NAME) }
        )
    private val mockPref: UserPreferences = UserPreferences(testDataStore)
    private var mockApi: ApiService = FakeApiService()
    private var mockDb: StoryDatabase = Room.inMemoryDatabaseBuilder(
        testContext,
        StoryDatabase::class.java
    ).allowMainThreadQueries().build()

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        val remoteMediator = StoryRemoteMediator(
            mockDb,
            mockApi,
            mockPref
        )
        val pagingState = PagingState<Int, StoryModel>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        TestCase.assertTrue(result is RemoteMediator.MediatorResult.Success)
        TestCase.assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
    }
}

class FakeApiService : ApiService {
    override suspend fun register(user: UserRegistrationModel): retrofit2.Response<Response> {
        TODO("Not yet implemented")
    }

    override suspend fun login(user: UserLoginModel): retrofit2.Response<ResponseLogin> {
        TODO("Not yet implemented")
    }

    override suspend fun sendStory(
        userToken: String,
        file: MultipartBody.Part,
        description: RequestBody,
        lat: Double?,
        lon: Double?
    ): retrofit2.Response<Response> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchStories(
        userToken: String,
        location: Int?,
        page: Int?,
        size: Int?
    ): ResponseStory = ResponseStory(
        false,
        DataDummy.generateDummyStoryResponse(),
        ""
    )
}

object DataDummy {
    fun generateDummyStoryResponse(): List<StoryModel> {
        val items: MutableList<StoryModel> = arrayListOf()
        for (i in 0..100) {
            val quote = StoryModel(
                "created $i",
                "des $i",
                i.toString(),
                0.0,
                0.1,
                "name $i",
                "photo $i"
            )
            items.add(quote)
        }
        return items
    }
}

const val PREFERENCES_NAME = "local_data"