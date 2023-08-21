package com.ptit.signlanguage.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ptit.signlanguage.network.api.ApiService
import com.ptit.signlanguage.base.BaseViewModel
import com.ptit.signlanguage.network.model.response.BaseResponse
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.network.model.response.VideoToTextResponse
import com.ptit.signlanguage.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainViewModel(private val apiService: ApiService) : BaseViewModel() {

    val videoToTextRes = MutableLiveData<BaseResponse<VideoToTextResponse?>?>()
    fun videoToText(file: File) {
        viewModelScope.launch {
            showLoading()
            val part = toMultipartBody("file", file)
            val result: BaseResponse<VideoToTextResponse?>?
            try {
                withContext(Dispatchers.IO) {
                    result = apiService.videoToText(part)
                }
                videoToTextRes.postValue(result)
            } catch (e: Exception) {
                handleApiError(e.cause)
            }
            hideLoading()
        }
    }

}