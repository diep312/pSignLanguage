package com.nguyencuong.mybasekotlin.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nguyencuong.mybasekotlin.network.api.RetrofitBuilder
import com.nguyencuong.mybasekotlin.ui.login.LoginViewModel
import com.nguyencuong.mybasekotlin.ui.main.MainViewModel

class ViewModelFactory() : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(RetrofitBuilder.apiService) as T
        }
        else if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(RetrofitBuilder.apiService) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}