package com.jonrysimbolonstory.application

import android.content.Context
import android.graphics.BitmapFactory
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.jonrysimbolonstory.BuildConfig
import com.jonrysimbolonstory.adapter.StoryAdapter
import com.jonrysimbolonstory.data.StoryRemoteMediator
import com.jonrysimbolonstory.data.StoryRepository
import com.jonrysimbolonstory.fragment.add.AddStoryViewModel
import com.jonrysimbolonstory.fragment.authentication.AuthenticationViewModel
import com.jonrysimbolonstory.fragment.authentication.login.LoginViewModel
import com.jonrysimbolonstory.fragment.authentication.registration.RegistrationViewModel
import com.jonrysimbolonstory.fragment.detail.DetailStoryViewModel
import com.jonrysimbolonstory.fragment.home.HomeViewModel
import com.jonrysimbolonstory.fragment.map.StoryMapsViewModel
import com.jonrysimbolonstory.local.StoryDatabase
import com.jonrysimbolonstory.model.UserPreferences
import com.jonrysimbolonstory.remote.ApiService
import com.jonrysimbolonstory.utils.BitmapLoader
import com.jonrysimbolonstory.utils.ImageUtils
import com.jonrysimbolonstory.utils.LoadingDialog
import com.jonrysimbolonstory.utils.STORY_DATABASE
import com.jonrysimbolonstory.utils.StoryFailureDialog
import com.jonrysimbolonstory.widget.StackRemoteViewsFactory
import com.jonrysimbolonstory.widget.StackWidgetViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = BuildConfig.PREFERENCES)


private val loggingInterceptor = with(HttpLoggingInterceptor()) {
    if (BuildConfig.DEBUG)
        setLevel(HttpLoggingInterceptor.Level.BODY)
    else
        setLevel(HttpLoggingInterceptor.Level.NONE)
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