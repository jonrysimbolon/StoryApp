package com.storyapp.application

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.storyapp.BuildConfig
import com.storyapp.fragment.add.AddStoryViewModel
import com.storyapp.fragment.authentication.AuthenticationViewModel
import com.storyapp.fragment.authentication.login.LoginViewModel
import com.storyapp.fragment.authentication.registration.RegistrationViewModel
import com.storyapp.fragment.home.HomeViewModel
import com.storyapp.main.MainViewModel
import com.storyapp.model.UserPreferences
import com.storyapp.remote.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
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

private val retrofit = with(Retrofit.Builder()) {
    baseUrl("https://story-api.dicoding.dev/v1/")
    addConverterFactory(GsonConverterFactory.create())
    client(client)
    build()
}

val appModule = module {
    single { retrofit.create(ApiService::class.java) }
    single { androidContext().dataStore }
    single { UserPreferences(get()) }
}

val viewModelModule = module {
    viewModel { MainViewModel(get()) }
    viewModel { AuthenticationViewModel() }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegistrationViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { AddStoryViewModel(get(), get()) }
}