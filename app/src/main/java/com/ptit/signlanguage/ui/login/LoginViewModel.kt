package com.ptit.signlanguage.ui.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.GoogleAuthUtil.getToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.ptit.signlanguage.base.BaseViewModel
import com.ptit.signlanguage.network.api.ApiService
import com.ptit.signlanguage.network.model.request.LoginRequest
import com.ptit.signlanguage.network.model.response.BaseResponse
import com.ptit.signlanguage.network.model.response.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val apiService: ApiService) : BaseViewModel() {

    var mtoken = MutableLiveData<String>()
    fun getToken(account: GoogleSignInAccount?, context : Context) {
        viewModelScope.launch {
            try {
                var token = ""
                withContext(Dispatchers.IO) {
                     token = getToken(context, account?.account!!,  "oauth2:profile email")
                }
                mtoken.postValue(token)
            } catch (e : Exception) {
                mtoken.postValue(e.message.toString())
            }
        }
    }

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
}