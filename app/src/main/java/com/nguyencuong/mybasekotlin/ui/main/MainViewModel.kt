package com.nguyencuong.mybasekotlin.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nguyencuong.mybasekotlin.network.api.ApiService
import com.nguyencuong.mybasekotlin.network.model.response.User
import com.nguyencuong.mybasekotlin.base.BaseViewModel
import com.nguyencuong.mybasekotlin.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(private val apiService: ApiService) : BaseViewModel() {

    var listUser = MutableLiveData< Resource<List<User>?> >()

    fun getUsers() {
        viewModelScope.launch {
            var result : List<User>? = null
            listUser.postValue(Resource.loading(null))
            try {
                withContext(Dispatchers.IO) {
                    result = apiService.getUsers()
                }
                listUser.postValue(Resource.success(result))
            } catch (e : Exception) {
                listUser.postValue(Resource.error(result, e.message))
            }
        }
    }

}