package com.ptit.signlanguage.ui.predict

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.CountDownTimer
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ptit.signlanguage.base.BaseViewModel
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import java.io.File
import java.io.File.separator
import java.io.FileOutputStream
import java.io.OutputStream


class RealtimeDetectVM: BaseViewModel() {
    private var _countdown: MutableLiveData<Long> = MutableLiveData(3)
    val countdown: LiveData<Long>
        get() = _countdown

    private val handler = Handler(Looper.getMainLooper())
    init {
        _countdown.value = 4000
    }
    var resultString: MutableLiveData<AnalysisResult> = MutableLiveData<AnalysisResult>()
    class AnalysisResult(val result: String)

    fun resetCounter() {
        _countdown.value = 4000
    }

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


}