package com.ptit.signlanguage.ui.main

import android.media.MediaMetadataRetriever
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ptit.signlanguage.base.BaseViewModel
import com.ptit.signlanguage.base.MyApplication.Companion.context
import com.ptit.signlanguage.network.api.ApiService
import com.ptit.signlanguage.network.api.RetrofitBuilder
import com.ptit.signlanguage.network.model.request.UpdateUserRequest
import com.ptit.signlanguage.network.model.response.*
import com.ptit.signlanguage.network.model.response.VideoToText.VideoToTextResponse
import com.ptit.signlanguage.network.model.response.check_video.CheckVideoRes
import com.ptit.signlanguage.network.model.response.score_with_subject.ScoreWithSubject
import com.ptit.signlanguage.network.model.response.subjectWrap.SubjectWrap
import com.ptit.signlanguage.ui.tensorflowdetect.Detection
import com.ptit.signlanguage.ui.tensorflowdetect.Prediction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileInputStream
import java.nio.charset.StandardCharsets
import kotlin.system.measureTimeMillis

class MainViewModel(private val apiService: ApiService) : BaseViewModel() {

    val videoToTextRes = MutableLiveData<VideoToTextResponse?>()
    val bestPredict = MutableLiveData<String?>()
    @RequiresApi(Build.VERSION_CODES.P)
    fun videoToText(file: File) {
//        viewModelScope.launch {
//            showLoading()
//            val part = toMultipartBody("video", file)
//            val result: VideoToTextResponse?
//            try {
//                withContext(Dispatchers.IO) {
//                    var time = measureTimeMillis {
//                        result = RetrofitBuilder.apiAiSide!!.videoToText(part)
//                        Log.d("TAG", result.toString())
//                    }
//                    Log.d("TAG", time.toString())
//
//                }
//                videoToTextRes.postValue(result)
//
//            } catch (e: Exception) {
//                Log.d("TAG", "$e")
//                handleApiError(e.cause)
//            }
//            hideLoading()
//        }
        // ======Handle on Mobile ========
        viewModelScope.launch {
            showLoading()
            val retriever = MediaMetadataRetriever()
            val inputStream = FileInputStream(file.absoluteFile)
            retriever.setDataSource(inputStream.fd)
            val framesArray = Detection.getListFrames(retriever)
            Detection.createClassifier(context)
            Detection.reset()
            var s = Prediction("None", 0f)
            for(i in 1..3){
                for(frame in framesArray){
                    s = Detection.processImage(context,frame)
                    delay(50)
                    Log.d("StreamVideoClassifier", s.label + " " + s.score)
                }
            }
            bestPredict.postValue(s.toString())
            Detection.reset()
            hideLoading()
        }
    }

    val listSubjectRes = MutableLiveData<BaseArrayResponse<Subject?>?>()
    fun getListSubject() {
        viewModelScope.launch {
            showLoading()
            val result: BaseArrayResponse<Subject?>?
            try {
                withContext(Dispatchers.IO) {
                    result = apiService.getListSubject()
                }
                listSubjectRes.postValue(result)
            } catch (e: Exception) {
                handleApiError(e.cause)
            }
            hideLoading()
        }
    }

    val listLabelRes = MutableLiveData<BaseArrayResponse<Label?>?>()
    fun getListLabel() {
        viewModelScope.launch {
            showLoading()
            val result: BaseArrayResponse<Label?>?
            try {
                withContext(Dispatchers.IO) {
                    result = apiService.getListLabel()
                }
                listLabelRes.postValue(result)
            } catch (e: Exception) {
                handleApiError(e.cause)
            }
            hideLoading()
        }
    }

    val videoRes = MutableLiveData<BaseResponse<Video?>?>()
    fun getVideo(label : String) {
        viewModelScope.launch {
            showLoading()
            val result: BaseResponse<Video?>?
            try {
                withContext(Dispatchers.IO) {
                    result = apiService.getVideo(label)
                }
                videoRes.postValue(result)
            } catch (e: Exception) {
                handleApiError(e.cause)
            }
            hideLoading()
        }
    }

    val subjectInfoRes = MutableLiveData<BaseResponse<SubjectWrap?>?>()
    fun getSubjectAllInfo(subjectID : Int) {
        viewModelScope.launch {
            showLoading()
            val result: BaseResponse<SubjectWrap?>?
            try {
                withContext(Dispatchers.IO) {
                    result = apiService.getAllInfoSubject(subjectID)
                }
                subjectInfoRes.postValue(result)
            } catch (e: Exception) {
                handleApiError(e.cause)
            }
            hideLoading()
        }
    }

    val updateUserRes = MutableLiveData<BaseResponse<User?>>()
    fun updateUser(updateUserRequest: UpdateUserRequest) {
        viewModelScope.launch {
            showLoading()
            val result: BaseResponse<User?>
            try {
                withContext(Dispatchers.IO) {
                    result = apiService.updateUser(updateUserRequest)
                }
                updateUserRes.postValue(result)
            } catch (e: Exception) {
                handleApiError(e.cause)
            }
            hideLoading()
        }
    }

    val checkVideoRes = MutableLiveData<VideoToTextResponse?>()
    fun checkVideo(file: File, label: String) {
        viewModelScope.launch {
            showLoading()
            val part = toMultipartBody("video", file)
            val result: VideoToTextResponse?
            try {
                withContext(Dispatchers.IO) {
                    result = RetrofitBuilder.apiAiSide!!.videoToText(part)
                }
                checkVideoRes.postValue(result)
            } catch (e: Exception) {
                handleApiError(e.cause)
            }
            hideLoading()
        }
    }

    val scoreWithSubject = MutableLiveData<BaseResponse<ScoreWithSubject?>?>()
    fun getScoreWithSubject(levelIds : Int, subjectIds : Int) {
        viewModelScope.launch {
            showLoading()
            val result: BaseResponse<ScoreWithSubject?>?
            try {
                withContext(Dispatchers.IO) {
                    result = apiService.getScoreWithSubject(levelIds, subjectIds)
                }
                scoreWithSubject.postValue(result)
            } catch (e: Exception) {
                handleApiError(e.cause)
            }
            hideLoading()
        }
    }
}