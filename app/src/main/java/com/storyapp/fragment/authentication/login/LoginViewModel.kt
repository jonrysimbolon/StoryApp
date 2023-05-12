package com.storyapp.fragment.authentication.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.storyapp.model.UserLoginModel
import com.storyapp.model.UserModel
import com.storyapp.model.UserPreferences
import com.storyapp.remote.ApiService
import com.storyapp.remote.response.LoginResult
import com.storyapp.remote.response.ResponseLogin
import com.storyapp.remote.response.ResultStatus
import kotlinx.coroutines.launch

class LoginViewModel(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    fun login(userLoginModel: UserLoginModel): LiveData<ResultStatus<ResponseLogin>> = liveData {
        emit(ResultStatus.Loading)
        try {
            val loginResponse = apiService.login(userLoginModel)
            if (!loginResponse.error) {
                emit(ResultStatus.Success(loginResponse))
            } else {
                emit(ResultStatus.Error(loginResponse.message))
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