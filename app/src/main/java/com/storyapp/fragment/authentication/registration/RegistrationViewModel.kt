package com.storyapp.fragment.authentication.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.storyapp.model.UserRegistrationModel
import com.storyapp.remote.ApiService
import com.storyapp.remote.response.Response
import com.storyapp.remote.response.ResultStatus

class RegistrationViewModel(
    private val apiService: ApiService,

) : ViewModel() {

    fun register(userRegistrationModel: UserRegistrationModel): LiveData<ResultStatus<Response>> = liveData {
        emit(ResultStatus.Loading)
        try {
            val registrationResponse = apiService.register(userRegistrationModel)
            if(!registrationResponse.error){
                emit(ResultStatus.Success(registrationResponse))
            }else{
                emit(ResultStatus.Error(registrationResponse.message))
            }
        }catch (e: Exception){
            e.printStackTrace()
            emit(ResultStatus.Error(e.message.toString()))
        }
    }
}