package com.ptit.signlanguage.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.network.api.RetrofitBuilder
import com.ptit.signlanguage.ui.login.LoginViewModel
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.predict.RealtimeDetectVM
import com.ptit.signlanguage.ui.score.PracticeViewModel

object ViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(RetrofitBuilder.getApiService()!!) as T
        }
        else if(modelClass.isAssignableFrom(RealtimeDetectVM::class.java)){
            return RealtimeDetectVM() as T
        }
        else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(RetrofitBuilder.apiServiceLogin) as T
        }
        else if(modelClass.isAssignableFrom(PracticeViewModel::class.java)){
            return PracticeViewModel(RetrofitBuilder.getApiService()!!) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}