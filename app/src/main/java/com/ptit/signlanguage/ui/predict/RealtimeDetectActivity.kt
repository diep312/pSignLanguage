package com.ptit.signlanguage.ui.predict

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.databinding.ActivityPredictionBinding
import com.ptit.signlanguage.view_model.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class RealtimeDetectActivity: BaseActivity<RealtimeDetectVM, ActivityPredictionBinding>() {

    private lateinit var cameraExecutor: ExecutorService
    private var mLastAnalysisResultTime: Long = 0
    private var currentSide_Camera = BACK_CAMERA
    private var permissionGranted: Boolean = false
    private var recording: Recording? = null
    private var recordState: Boolean = false
    private var modeRecording: Boolean = false
    private val selector by lazy {
        QualitySelector.from(
        Quality.HD,
        FallbackStrategy.higherQualityOrLowerThan(Quality.SD)
    ) }
    private val recorder by lazy {
        Recorder.Builder()
            .setQualitySelector(selector)
            .build()
    }
    private val videoCapture by lazy { VideoCapture.withOutput(recorder) }
    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[RealtimeDetectVM::class.java]
    }

    override fun getContentLayout(): Int = R.layout.activity_prediction

    override fun initView() {
        setColorForStatusBar(R.color.color_status_camera)
        binding.apply {
            countDown.visibility = View.GONE
            backbtn.setOnClickListener{
                onBackPressedDispatcher.onBackPressed()
            }
            getCameraPermission()
            if(permissionGranted){
                cameraExecutor = Executors.newSingleThreadExecutor()
                startCamera()
            }else{
                Toast.makeText(applicationContext,"You have to grant permission",Toast.LENGTH_SHORT).show()
                finish()
            }
            flip.setOnClickListener{
                currentSide_Camera = currentSide_Camera xor 1
                startCamera()
            }
            recordbtn.setOnClickListener{
                if(recordState){
                    recording?.stop()
                    recordState = false
                    recordbtn.setImageResource(R.drawable.recording1)

                }else{
                    if(modeRecording){
                        binding.countDown.visibility = View.VISIBLE
                        viewModel.startTimer()
                        Handler().postDelayed(
                            {
                                initRecorder()
                                recordbtn.setImageResource(R.drawable.recording)
                                recordState = true
                                binding.countDown.visibility = View.GONE
                        }, 3000)

                    }else{
                        initRecorder()
                        recordbtn.setImageResource(R.drawable.recording)
                        recordState = true
                    }
                }
            }
            manual.setOnClickListener{
                showManualDialog()
            }
            recordMode.setOnClickListener{
                if(!modeRecording){
                    recordMode.setImageResource(R.drawable.group)
                    modeRecording = true
                }else{
                    recordMode.setImageResource(R.drawable.group_unclick)
                    modeRecording = false
                }
            }
        }
    }

    private fun showManualDialog(){
        val dialog = ManualDialogFragment()
        dialog.show(supportFragmentManager, "Test")

    }
    @OptIn(UnstableApi::class)
    private fun initRecorder(){
        val recordingListener = Consumer<VideoRecordEvent> { event ->
            when(event) {
                is VideoRecordEvent.Start -> {
                    val msg = "Capture Started"
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT)
                        .show()
                }
                is VideoRecordEvent.Finalize -> {
                    val msg = if (!event.hasError()) {
                        "Video capture succeeded"
                            .also {
                                viewModel.resultString.postValue(RealtimeDetectVM.AnalysisResult(event.outputResults.outputUri.path  ?: "null"))
                                returnURI(event.outputResults.outputUri)
                            }
                    } else {
                        // update app state when the capture failed.
                        recording?.close()
                        recording = null

                        "Video capture ends with error: ${event.error}"
                    }
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "${Date().time}")
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        }

        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()
        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .start(ContextCompat.getMainExecutor(this), recordingListener)


    }
    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewCamera.surfaceProvider)
                }
            var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            if(currentSide_Camera == FRONT_CAMERA){
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            }

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, videoCapture, preview
                )

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    override fun initListener() {
    }

    override fun observerLiveData() {
        viewModel.apply {
            countdown.observe(this@RealtimeDetectActivity) {
                binding.countDown.text = (it/1000).toString()
            }
        }
    }
    private fun getCameraPermission(){
        if(ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                VIDEO_RECORD_CODE
            )
        }else{
            permissionGranted = true
        }
    }
    companion object {
        private const val CAMERA_PERMISSION_CODE: Int = 100
        private const val VIDEO_RECORD_CODE: Int = 101
        private const val REQUEST_TAKE_GALLERY_VIDEO: Int = 102
        private const val BACK_CAMERA = 1
        private const val FRONT_CAMERA = 0
        private const val imageSize = 96
    }
    private fun returnURI(uri: Uri){
        val data = Intent()
        data.setData(uri)
        Log.d("Link", uri.toString())
        setResult(RESULT_OK, data )
        this.finish()
    }


}