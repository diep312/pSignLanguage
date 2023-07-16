package com.nguyencuong.mybasekotlin.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    val isLoading = MutableLiveData<Boolean>().apply { value = false }

    fun hideLoading() {
        isLoading.value = false
    }

    fun showLoading() {
        isLoading.value = true
    }
}