package com.story.fragment.authentication.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.story.model.UserLoginModel
import com.story.model.UserModel
import com.story.model.UserPreferences
import com.story.remote.ApiService
import com.story.remote.response.LoginResult
import com.story.remote.response.ResponseLogin
import com.story.utils.Event
import com.story.utils.ResultStatus
import com.story.utils.responseGsonPattern
import com.story.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.launch

class LoginViewModel(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences,
    private val gson: Gson
) : ViewModel() {

    private val _isValidLogin: MutableLiveData<Event<ResultStatus<ResponseLogin>>> =
        MutableLiveData()
    val isValidLogin: LiveData<Event<ResultStatus<ResponseLogin>>> get() = _isValidLogin

    fun login(userLoginModel: UserLoginModel) {
        wrapEspressoIdlingResource {
            viewModelScope.launch {
                _isValidLogin.postValue(Event(ResultStatus.Loading))
                try {
                    val loginResponse = apiService.login(userLoginModel)
                    val loginBody = loginResponse.body()
                    if (loginResponse.isSuccessful && loginBody != null) {
                        _isValidLogin.postValue(Event(ResultStatus.Success(loginBody)))
                    } else {
                        _isValidLogin.postValue(
                            Event(
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
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    _isValidLogin.postValue(Event(ResultStatus.Error(e.message.toString())))
                }
            }
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