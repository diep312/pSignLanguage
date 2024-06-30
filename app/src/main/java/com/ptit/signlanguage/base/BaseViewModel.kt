package com.ptit.signlanguage.base

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ptit.signlanguage.R
import com.ptit.signlanguage.network.model.response.Message
import com.ptit.signlanguage.utils.GsonUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import javax.net.ssl.HttpsURLConnection

abstract class BaseViewModel : ViewModel() {
    val isLoading = MutableLiveData<Boolean>().apply { value = false }

    fun hideLoading() {
        isLoading.value = false
    }

    fun showLoading() {
        isLoading.value = true
    }

    val errorMessage: MutableLiveData<Int> = MutableLiveData()
    val responseMessage: MutableLiveData<String?> = MutableLiveData()

    protected fun handleApiError(error: Throwable?) {
        if (error == null) {
            errorMessage.postValue(R.string.api_default_error)
            return
        }

        if (error is HttpException) {
            Log.w("ERROR", error.message() + error.code())
            when (error.code()) {
                HttpURLConnection.HTTP_BAD_REQUEST -> try {
                    val message: Message? = GsonUtils.deserialize(
                        error.response()?.errorBody()?.string(),
                        Message::class.java
                    )
                    responseMessage.postValue(message?.message)
                } catch (e: IOException) {
                    e.printStackTrace()
                    responseMessage.postValue(error.message)
                }
                HttpsURLConnection.HTTP_UNAUTHORIZED -> errorMessage.postValue(R.string.str_authe)
                HttpsURLConnection.HTTP_FORBIDDEN, HttpsURLConnection.HTTP_INTERNAL_ERROR, HttpsURLConnection.HTTP_NOT_FOUND -> errorMessage.postValue(
                    R.string.api_default_error
                )
                else -> responseMessage.postValue(error.message)
            }
        } else if (error is SocketTimeoutException) {
            errorMessage.postValue(R.string.text_all_has_error_timeout)
        } else if (error is IOException) {
            Log.e("TAG", error.message.toString())
            errorMessage.postValue(R.string.text_all_has_error_network)
        } else {
            if (!TextUtils.isEmpty(error.message)) {
                responseMessage.postValue(error.message)
            } else {
                errorMessage.postValue(R.string.text_all_has_error_please_try)
            }
        }
    }

    fun toMultipartBody(name: String, file: File): MultipartBody.Part {
        val reqFile = file.asRequestBody("video/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(name, file.name, reqFile)
    }
}