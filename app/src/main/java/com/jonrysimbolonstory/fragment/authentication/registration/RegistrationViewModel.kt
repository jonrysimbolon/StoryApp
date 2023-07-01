package com.jonrysimbolonstory.fragment.authentication.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jonrysimbolonstory.model.UserRegistrationModel
import com.jonrysimbolonstory.remote.ApiService
import com.jonrysimbolonstory.remote.response.Response
import com.jonrysimbolonstory.utils.ResultStatus
import com.jonrysimbolonstory.utils.responseGsonPattern
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val apiService: ApiService,
    private val gson: Gson

) : ViewModel() {

    private val _isValidRegister: MutableLiveData<ResultStatus<Response>> = MutableLiveData()
    val isValidRegister: LiveData<ResultStatus<Response>> get() = _isValidRegister

    fun register(userRegistrationModel: UserRegistrationModel) {
        viewModelScope.launch {
            _isValidRegister.postValue(ResultStatus.Loading)
            try {
                val registrationResponse = apiService.register(userRegistrationModel)
                val registrationBody = registrationResponse.body()
                if (registrationResponse.isSuccessful && registrationBody != null) {
                    _isValidRegister.postValue(ResultStatus.Success(registrationBody))
                } else {
                    _isValidRegister.postValue(
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
                _isValidRegister.postValue(
                    ResultStatus.Error(e.message.toString())
                )
            }
        }
    }
}