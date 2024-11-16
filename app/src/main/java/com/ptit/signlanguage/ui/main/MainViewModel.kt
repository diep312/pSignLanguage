package com.ptit.signlanguage.ui.main

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Since
import com.ptit.signlanguage.BuildConfig
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
import com.ptit.signlanguage.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Singleton
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

    suspend fun getVideoFlow(label: String): String {
        return apiService.getVideo(label)?.body?.video_url ?: ""
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
                    Log.d("UserScore", "Updated $score")
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

    val listSubjectWithProgress = MutableStateFlow<List<Subject?>>(listOf())

    fun getSubjectWithProgress() =
        viewModelScope.launch {
            showLoading()

            val subjects = apiService.getListSubject()?.body ?: emptyList()

            val listDetailSubject: MutableList<Subject?> = subjects
                    .map { subject ->
                        async {
                            subject?.let {
                                val detailSubject = apiService.getUserProgress(it.id).body
                                // Return the updated subject with additional details
                                val result = it.copy(
                                    id = it.id,
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

    fun getGeminiResponse(language: String, word: String) = callbackFlow<String> {
        val inputEN = "What does $word means and give a sentence using it." +
                " The output same like: Meaning: To speak negatively or critically about someone or something, often in a malicious or hurtful way. It can also be used to mean slander or defame.\n" +
                "\n" +
                "Example:\n" +
                "\n" +
                "\"...\"\n"
        val inputVN = "$word nghĩa là gì và cho một câu ví dụ về từ đó. " +
                "Câu trả lời dạng: Định nghĩa: Là ....\n" +
                "\n" +
                "Ví dụ:\n" +
                "\n" +
                "\"...\"\n"
        val model = GenerativeModel(
            modelName = "gemini-1.5-flash-8b",
            apiKey = Constants.API_GEMINI,
            generationConfig = generationConfig {
                temperature = 0.15f
                topK = 32
                topP = 1f
                maxOutputTokens = 4096
            },
        )
        try {
            model.generateContentStream(if (language == Constants.EN) inputEN else inputVN)
                .flowOn(Dispatchers.IO)
                .distinctUntilChanged()
                .collect { response ->
                    if (response.text?.isNotEmpty() == true) {
                        trySend(response.text ?: "")
                    }
                }
        }catch (e: Exception){
            trySend("Server error, please try again")
        }
        awaitClose()
    }


    protected suspend fun predictLabel(
        file: File
    ): Prediction? {
        val part = toMultipartBody("video", file)
        val result: Prediction
        try {
            withContext(Dispatchers.IO) {
                measureTimeMillis {
                    val response = RetrofitBuilder.apiAiSide!!.videoToText(part)
                    result = Prediction(
                        prediction = response?.prediction ?: "",
                        accuracy = response?.score ?: 0.0
                    )
                }
            }
            return result
        } catch (e: Exception) {
            Log.d("TAG", "$e")
            handleApiError(e.cause)
        }
        return null
    }
}
