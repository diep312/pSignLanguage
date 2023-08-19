package com.ptit.signlanguage.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ptit.signlanguage.network.api.ApiService
import com.ptit.signlanguage.base.BaseViewModel
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.utils.Resource
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