package com.storyapp.fragment.authentication.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.storyapp.model.UserLoginModel
import com.storyapp.model.UserModel
import com.storyapp.model.UserPreferences
import com.storyapp.remote.ApiService
import com.storyapp.remote.response.LoginResult
import com.storyapp.remote.response.ResponseLogin
import com.storyapp.remote.response.ResultStatus
import com.storyapp.utils.responseGsonPattern
import kotlinx.coroutines.launch

class LoginViewModel(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences,
    private val gson: Gson
) : ViewModel() {

    fun login(userLoginModel: UserLoginModel): LiveData<ResultStatus<ResponseLogin>> = liveData {
        emit(ResultStatus.Loading)
        try {
            val loginResponse = apiService.login(userLoginModel)
            val loginBody = loginResponse.body()
            if (loginResponse.isSuccessful && loginBody != null) {
                emit(ResultStatus.Success(loginBody))
            } else {
                emit(
                    ResultStatus.Error(
                        responseGsonPattern(
                            gson,
                            loginResponse
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

    fun saveUser(response: LoginResult) {
        viewModelScope.launch {
            userPreferences.saveUser(
                UserModel(
                    userId = response.userId,
                    name = response.name,
                    token = response.token,
                    isLogin = true
                )
            )
        }
    }
}