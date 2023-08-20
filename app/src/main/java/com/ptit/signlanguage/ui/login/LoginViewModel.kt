package com.ptit.signlanguage.ui.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.GoogleAuthUtil.getToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.ptit.signlanguage.base.BaseViewModel
import com.ptit.signlanguage.network.api.ApiService
import com.ptit.signlanguage.network.model.request.LoginRequest
import com.ptit.signlanguage.network.model.request.RegisterRequest
import com.ptit.signlanguage.network.model.response.BaseResponse
import com.ptit.signlanguage.network.model.response.Token
import com.ptit.signlanguage.network.model.response.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val apiService: ApiService) : BaseViewModel() {

    val loginResponse = MutableLiveData<BaseResponse<User?>?>()
    fun login(email : String, password : String) {
        viewModelScope.launch {
            showLoading()
            val loginRequest = LoginRequest(username = email, password = password)
            val result: BaseResponse<User?>?
            try {
                withContext(Dispatchers.IO) {
                    result = apiService.login(loginRequest)
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
                    result = apiService.register(registerRequest)
                }
                registerResponse.postValue(result)
            } catch (e: Exception) {
                handleApiError(e.cause)
            }
            hideLoading()
        }
    }
}