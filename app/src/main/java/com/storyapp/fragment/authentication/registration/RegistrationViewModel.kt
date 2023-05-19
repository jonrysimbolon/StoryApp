package com.storyapp.fragment.authentication.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.storyapp.model.UserRegistrationModel
import com.storyapp.remote.ApiService
import com.storyapp.remote.response.Response
import com.storyapp.remote.response.ResultStatus
import com.storyapp.utils.responseGsonPattern

class RegistrationViewModel(
    private val apiService: ApiService,
    private val gson: Gson

) : ViewModel() {

    fun register(userRegistrationModel: UserRegistrationModel): LiveData<ResultStatus<Response>> =
        liveData {
            emit(ResultStatus.Loading)
            try {
                val registrationResponse = apiService.register(userRegistrationModel)
                val registrationBody = registrationResponse.body()
                if (registrationResponse.isSuccessful && registrationBody != null) {
                    emit(ResultStatus.Success(registrationBody))
                } else {
                    emit(
                        ResultStatus.Error(
                            responseGsonPattern(
                                gson,
                                registrationResponse
                                    .errorBody()
                                    ?.string()
                                    .toString()
                            ).message
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emit(ResultStatus.Error(e.message.toString()))
            }
        }
}