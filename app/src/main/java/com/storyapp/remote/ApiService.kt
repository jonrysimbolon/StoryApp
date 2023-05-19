package com.storyapp.remote

import com.storyapp.model.UserLoginModel
import com.storyapp.model.UserRegistrationModel
import com.storyapp.remote.response.Response
import com.storyapp.remote.response.ResponseLogin
import com.storyapp.remote.response.ResponseStory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiService {

    @Headers("Content-Type:application/json")
    @POST("v1/register")
    suspend fun register(@Body user: UserRegistrationModel): retrofit2.Response<Response>

    @Headers("Content-Type:application/json")
    @POST("v1/login")
    suspend fun login(@Body user: UserLoginModel): retrofit2.Response<ResponseLogin>

    @Multipart
    @POST("v1/stories")
    suspend fun sendStory(
        @Header("Authorization") userToken: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): retrofit2.Response<Response>

    @GET("v1/stories")
    suspend fun fetchStories(
        @Header("Authorization") userToken: String,
    ): retrofit2.Response<ResponseStory>

}
