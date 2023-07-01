package com.jonrysimbolonstory.fragment.authentication.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.jonrysimbolonstory.model.UserLoginModel
import com.jonrysimbolonstory.model.UserModel
import com.jonrysimbolonstory.model.UserPreferences
import com.jonrysimbolonstory.remote.ApiService
import com.jonrysimbolonstory.remote.response.LoginResult
import com.jonrysimbolonstory.remote.response.ResponseLogin
import com.jonrysimbolonstory.utils.Event
import com.jonrysimbolonstory.utils.ResultStatus
import com.jonrysimbolonstory.utils.responseGsonPattern
import com.jonrysimbolonstory.utils.wrapEspressoIdlingResource
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