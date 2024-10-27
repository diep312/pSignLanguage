package com.ptit.signlanguage.ui.main

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import com.ptit.signlanguage.base.BaseViewModel
import com.ptit.signlanguage.network.api.ApiService
import com.ptit.signlanguage.network.api.RetrofitBuilder
import com.ptit.signlanguage.network.model.request.UpdateUserRequest
import com.ptit.signlanguage.network.model.response.*
import com.ptit.signlanguage.network.model.response.VideoToText.Prediction
import com.ptit.signlanguage.network.model.response.VideoToText.VideoToTextResponse
import com.ptit.signlanguage.network.model.response.score_with_subject.ScoreWithSubject
import com.ptit.signlanguage.network.model.response.score_with_subject.UserScore
import com.ptit.signlanguage.network.model.response.subjectWrap.SubjectWrap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.system.measureTimeMillis

open class MainViewModel(
    private val apiService: ApiService,
) : BaseViewModel() {
    val videoToTextRes = MutableLiveData<Prediction?>()
    val bestPredict = MutableLiveData<String?>()

    @RequiresApi(Build.VERSION_CODES.P)
    fun videoToText(
        file: File,
        context: Context,
        label: String,
    ) {
        viewModelScope.launch {
            showLoading()
            val part = toMultipartBody("video", file)
            val result: VideoToTextResponse?
            try {
                withContext(Dispatchers.IO) {
                    var time =
                        measureTimeMillis {
                            result = RetrofitBuilder.apiAiSide!!.videoToText(part)
                        }
                    Log.d("TAG", time.toString())
                }
                bestPredict.postValue(result?.prediction)
            } catch (e: Exception) {
                Log.d("TAG", "$e")
                handleApiError(e.cause)
            }
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

    fun getVideo(label: String) {
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

    fun getSubjectAllInfo(subjectID: Int) {
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

    val scoreWithSubject = MutableLiveData<BaseResponse<ScoreWithSubject?>?>()

    fun getScoreWithSubject(
        levelIds: Int,
        subjectIds: Int,
    ) {
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

    fun updateUserScore(
        labelId: Int,
        score: Float,
    ) {
        viewModelScope.launch {
            showLoading()
            val result: BaseResponse<UserScore>
            try {
                withContext(Dispatchers.IO) {
                    Log.d("UserScore", "Updated " + score.toString())
                    result = apiService.postUserScore(labelId, score)
                    Log.d("UserScore", "Updated " + result.body?.score.toString())
                }
            } catch (e: Exception) {
                handleApiError(e.cause)
                Log.d("UserScore", e.toString())
            }
            hideLoading()
        }
    }

    val topUsers = MutableLiveData<BaseArrayResponse<UserScore>?>()

    fun getTopScoreOfLabel(labelId: Int) {
        viewModelScope.launch {
            showLoading()
            val result: BaseArrayResponse<UserScore>?
            try {
                withContext(Dispatchers.IO) {
                    result = apiService.getTopUserScoreOfLabel(labelId)
                }
                topUsers.postValue(result)
                Log.d(
                    "TopScore",
                    result!!
                        .body
                        ?.get(0)!!
                        .score
                        .toString(),
                )
            } catch (e: Exception) {
                handleApiError(e.cause)
            }
            hideLoading()
        }
    }

    data class ProgressTrack(
        @SerializedName("totalScore")
        val totalScore: Double,
        @SerializedName("numberOfLearnedLabels")
        val signs: Int,
    )

    val userProgress = MutableLiveData<ProgressTrack>()

    fun getUserProgress() {
        viewModelScope.launch {
            showLoading()
            withContext(Dispatchers.IO) {
                val result = apiService.getUserProgress().body
                result.let { res ->
                    userProgress.postValue(
                        ProgressTrack(
                            totalScore = res.totalScore,
                            signs = res.signs,
                        ),
                    )
                }
            }
            hideLoading()
        }
    }

    data class SubjectResult(
        val numLearnedLabels: Int,
        val totalLabels: Int,
    )

    val userSubjectProgress = MutableLiveData<SubjectResult>()

    fun getUserSubjectProgress(subjectId: Int) {
        viewModelScope.launch {
            showLoading()
            withContext(Dispatchers.IO) {
                val result = apiService.getUserProgress(subjectId).body
                result.let {
                    userSubjectProgress.postValue(
                        SubjectResult(
                            numLearnedLabels = it.numLearnedLabels,
                            totalLabels = it.totalLabels,
                        ),
                    )
                }
            }
            hideLoading()
        }
    }

    val listSubjectWithProgress = MutableStateFlow<List<Subject?>>(listOf())

    fun getSubjectWithProgress() =
        viewModelScope.launch {
            showLoading()

            val subjects = apiService.getListSubject()?.body ?: emptyList()

            var listDetailSubject: MutableList<Subject?> = mutableListOf()

            listDetailSubject =
                subjects
                    .map { subject ->
                        async {
                            subject?.let {
                                val detailSubject = apiService.getUserProgress(it.id).body
                                // Return the updated subject with additional details
                                val result = it.copy(
                                    totalLabels = detailSubject.totalLabels,
                                    learnedLabels = detailSubject.numLearnedLabels,
                                )
                                result
                            }
                        }
                    }.awaitAll()
                    .toMutableList()
            listSubjectWithProgress.value = listDetailSubject
            hideLoading()
        }
//    fun getSubjectWithProgress() = flow {
//        showLoading()
//        val subjects = apiService.getListSubject()?.body ?: emptyList()
//        subjects.asFlow() // Convert list to flow
//            .onEach { subject ->
//                subject?.let {
//                    val detailSubject = apiService.getUserProgress(it.id).body
//                    emit(
//                        subject.copy(
//                            totalLabels = detailSubject.totalLabels,
//                            learnedLabels = detailSubject.numLearnedLabel,
//                        )
//                    )
//                }
//            }
//            .onCompletion {
//                hideLoading()
//            }
//            .launchIn(viewModelScope)
//    }.catch { e ->
//        // Handle exceptions, or emit a state if needed
//        hideLoading()
//        println("Error occurred: ${e.message}")
//    }
}
