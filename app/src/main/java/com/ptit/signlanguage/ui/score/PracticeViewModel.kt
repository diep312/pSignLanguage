package com.ptit.signlanguage.ui.score

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ptit.signlanguage.base.BaseViewModel
import com.ptit.signlanguage.network.api.ApiService
import com.ptit.signlanguage.network.api.RetrofitBuilder
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.ui.tensorflowdetect.Prediction
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

class PracticeViewModel(apiService: ApiService) : MainViewModel(apiService) {
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
    class AnalysisResult(val result: String)

    fun startTimer() {
        handler.post(
            object : Runnable{
                override fun run() {
                    if (_countdown.value!! > 1) {
                        handler.postDelayed(this, 1000)
                        _countdown.value = _countdown.value!! - 1000
                    } else {
                        _countdown.value = 1
                    }
                }
            }
        )
    }
    private fun setScore(prediction: Prediction, labelChosen: String) {
        when (prediction.label) {
            labelChosen.lowercase(Locale.ROOT) -> {
                _score.value = (prediction.score * 100 ).toInt()
            }
            else -> {
                _score.value = 0
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.P)
    fun predictVideo(file: File, context: Context, label: String){
        viewModelScope.launch{
            showLoading()
            val predict = predictLabel(file, context)
            setScore(predict, label)
            hideLoading()
        }
    }
}