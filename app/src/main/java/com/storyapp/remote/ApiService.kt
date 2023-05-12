package com.storyapp.remote

import com.storyapp.model.UserLoginModel
import com.storyapp.model.UserRegistrationModel
import com.storyapp.remote.response.Response
import com.storyapp.remote.response.ResponseLogin
import com.storyapp.remote.response.ResponseStory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


interface ApiService {

    @Headers("Content-Type:application/json")
    @POST("register")
    suspend fun register(@Body user: UserRegistrationModel): Response

    @Headers("Content-Type:application/json")
    @POST("login")
    suspend fun login(@Body user: UserLoginModel): ResponseLogin

    @FormUrlEncoded
    @POST("/stories")
    suspend fun sendStory(
        @Header("Authorization") userToken: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Response

    @GET("/stories")
    suspend fun fetchStories(
        @Header("Authorization") userToken: String,
    ): ResponseStory

    @GET("/stories")
    suspend fun getStory(
        @Header("Authorization") userToken: String,
        @Path("id") id: String,
    ): Response
}
