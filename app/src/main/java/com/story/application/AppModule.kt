package com.story.application

import android.content.Context
import android.graphics.BitmapFactory
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.story.BuildConfig
import com.story.adapter.StoryAdapter
import com.story.data.StoryRemoteMediator
import com.story.data.StoryRepository
import com.story.fragment.add.AddStoryViewModel
import com.story.fragment.authentication.AuthenticationViewModel
import com.story.fragment.authentication.login.LoginViewModel
import com.story.fragment.authentication.registration.RegistrationViewModel
import com.story.fragment.detail.DetailStoryViewModel
import com.story.fragment.home.HomeViewModel
import com.story.fragment.map.StoryMapsViewModel
import com.story.local.StoryDatabase
import com.story.model.UserPreferences
import com.story.remote.ApiService
import com.story.utils.BitmapLoader
import com.story.utils.ImageUtils
import com.story.utils.LoadingDialog
import com.story.utils.STORY_DATABASE
import com.story.utils.StoryFailureDialog
import com.story.widget.StackRemoteViewsFactory
import com.story.widget.StackWidgetViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = BuildConfig.PREFERENCES)


private val loggingInterceptor = with(HttpLoggingInterceptor()) {
    if (BuildConfig.DEBUG) {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    } else {
        setLevel(HttpLoggingInterceptor.Level.NONE)
    }
}

private val client = with(OkHttpClient.Builder()) {
    addInterceptor(loggingInterceptor)
    build()
}

fun retrofit(url: String): Retrofit = with(Retrofit.Builder()) {
    baseUrl(url)
    addConverterFactory(GsonConverterFactory.create())
    client(client)
    build()
}

fun retrofitModule(url: String) = module {
    single { retrofit(url).create(ApiService::class.java) }
}

val localModule = module {
    single {
        Room.databaseBuilder(androidContext(), StoryDatabase::class.java, STORY_DATABASE)
            .fallbackToDestructiveMigration().build()
    }
}

val dataStoreModule = module {
    single { androidContext().dataStore }
    single { UserPreferences(get()) }
}

val gsonModule = module {
    single { Gson() }
}

val glideModule = module {
    single { Glide.with(androidContext()).setDefaultRequestOptions(get()) }
    single { RequestOptions() }
}

val bitmapModule = module {
    single { ImageUtils() }
    single { BitmapFactory.Options() }
    single { BitmapLoader(get()) }
}

val adapterModule = module {
    single { StoryAdapter(get()) }
}

val repositoryModule = module {
    single { StoryRemoteMediator(get(), get(), get()) }
    single { StoryRepository(get(), get()) }
}

val factoryModule = module {
    factory { StackRemoteViewsFactory(get(), get(), get()) }
}

val viewModelModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegistrationViewModel)
    viewModelOf(::AuthenticationViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::AddStoryViewModel)
    viewModelOf(::DetailStoryViewModel)
    viewModelOf(::StoryMapsViewModel)
    viewModelOf(::StackWidgetViewModel)
}

val customDialogModule = module {
    single { LoadingDialog() }
    single { StoryFailureDialog() }
}