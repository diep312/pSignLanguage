package com.ptit.signlanguage.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ptit.signlanguage.base.BaseViewModel
import com.ptit.signlanguage.network.api.ApiService
import com.ptit.signlanguage.network.api.ApiServiceLogin
import com.ptit.signlanguage.network.model.request.LoginRequest
import com.ptit.signlanguage.network.model.request.RegisterRequest
import com.ptit.signlanguage.network.model.response.BaseResponse
import com.ptit.signlanguage.network.model.response.Token
import com.ptit.signlanguage.network.model.response.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val apiService: ApiService, private val apiServiceLogin: ApiServiceLogin) : BaseViewModel() {

    val loginResponse = MutableLiveData<BaseResponse<User?>?>()
    fun login(email : String, password : String) {
        viewModelScope.launch {
            showLoading()
            val loginRequest = LoginRequest(email = email, password = password)
            val result: BaseResponse<User?>?
            try {
                withContext(Dispatchers.IO) {
                    result = apiServiceLogin.login(loginRequest)
                }
                loginResponse.postValue(result)
            } catch (e: Exception) {
                handleApiError(e.cause)
            }
            hideLoading()
        }
    }

    val registerResponse = MutableLiveData<BaseResponse<Token?>?>()
    fun register(name : String, email : String, password : String) {
        viewModelScope.launch {
            showLoading()
            val registerRequest = RegisterRequest(email, name, password)
            val result: BaseResponse<Token?>?
            try {
                withContext(Dispatchers.IO) {
                    result = apiServiceLogin.register(registerRequest)
                }
                registerResponse.postValue(result)
            } catch (e: Exception) {
                handleApiError(e.cause)
            }
            hideLoading()
        }
    }
}