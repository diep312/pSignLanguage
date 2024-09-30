package com.ptit.signlanguage.ui.score

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ptit.signlanguage.network.api.ApiService
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.tensorflowdetect.Prediction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

class PracticeViewModel(
    apiService: ApiService,
) : MainViewModel(apiService) {
    private var _countdown: MutableLiveData<Long> = MutableLiveData(3)
    val countdown: LiveData<Long>
        get() = _countdown

    private val handler = Handler(Looper.getMainLooper())

    private var _score: MutableLiveData<Int> = MutableLiveData(-1)
    val score: LiveData<Int>
        get() = _score

    init {
        _countdown.value = 4000
    }

    var resultString: MutableLiveData<AnalysisResult> = MutableLiveData<AnalysisResult>()

    class AnalysisResult(
        val result: String,
    )
    fun resetCounter() {
        _countdown.value = 4000
    }
    fun startTimer() {
        handler.post(
            object : Runnable {
                override fun run() {
                    if (_countdown.value!! > 1) {
                        handler.postDelayed(this, 1000)
                        _countdown.value = _countdown.value!! - 1000
                    } else {
                        _countdown.value = 1
                    }
                }
            },
        )
    }

    private fun setScore(
        prediction: Prediction,
        labelChosen: String,
    ) {
        if(prediction.label.lowercase(Locale.ROOT).trim() == labelChosen.lowercase(Locale.ROOT).trim()) {
            _score.postValue( (prediction.score * 100).toInt())
        }
        else{
            _score.postValue(0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun predictVideo(
        file: File,
        context: Context,
        label: String,
        labelId: Int
    ) {
        viewModelScope.launch {
            showLoading()
            withContext(Dispatchers.Default){
//                val predict = predictLabel(file, context, "labels.txt")
//                setScore(predict, label)
            }
//            updateUserScore(labelId, predict.score)
            hideLoading()
        }
    }
}
