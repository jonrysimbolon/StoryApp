package com.story.remote

import com.story.model.UserLoginModel
import com.story.model.UserRegistrationModel
import com.story.remote.response.Response
import com.story.remote.response.ResponseLogin
import com.story.remote.response.ResponseStory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


interface ApiService {

    @Headers("Content-Type:application/json")
    @POST("register")
    suspend fun register(@Body user: UserRegistrationModel): retrofit2.Response<Response>

    @Headers("Content-Type:application/json")
    @POST("login")
    suspend fun login(@Body user: UserLoginModel): retrofit2.Response<ResponseLogin>

    @Multipart
    @POST("stories")
    suspend fun sendStory(
        @Header("Authorization") userToken: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: Double? = null,
        @Part("lon") lon: Double? = null,
    ): retrofit2.Response<Response>

    @GET("stories")
    suspend fun fetchStories(
        @Header("Authorization") userToken: String,
        @Query("location") location: Int? = null,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
    ): ResponseStory

}
