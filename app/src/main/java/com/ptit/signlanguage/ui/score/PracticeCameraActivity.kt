package com.ptit.signlanguage.ui.score

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
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
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.databinding.ActivityPracticeBinding
import com.ptit.signlanguage.ui.predict.ConfirmationDialogFragment
import com.ptit.signlanguage.ui.predict.ManualDialogFragment
import com.ptit.signlanguage.ui.predict.RealtimeDetectActivity
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.view_model.ViewModelFactory
import java.io.File
import java.util.Date
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PracticeCameraActivity :
    BaseActivity<PracticeViewModel, ActivityPracticeBinding>(),
    ConfirmationDialogFragment.VideoPreviewListener {
    private var label: String? = null
    private var videoPath: String? = null
    private lateinit var cameraExecutor: ExecutorService
    private var mLastAnalysisResultTime: Long = 0
    private var currentSide_Camera = PracticeCameraActivity.BACK_CAMERA
    private var permissionGranted: Boolean = false
    private var recording: Recording? = null
    private var recordState: Boolean = false
    private var modeRecording: Boolean = false
    private lateinit var currentVideoUri: Uri

    private val selector by lazy {
        QualitySelector.from(
            Quality.HD,
            FallbackStrategy.higherQualityOrLowerThan(Quality.SD),
        )
    }

    private val recorder by lazy {
        Recorder
            .Builder()
            .setQualitySelector(selector)
            .build()
    }

    private val videoCapture by lazy { VideoCapture.withOutput(recorder) }

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[PracticeViewModel::class.java]
    }

    override fun getContentLayout(): Int = R.layout.activity_practice

    override fun initView() {
        setLightIconStatusBar(true)
        setColorForStatusBar(R.color.color_bg)
        binding.rootLayout.setPadding(0, getStatusBarHeight(this@PracticeCameraActivity), 0, 0)

        label = intent.getStringExtra(Constants.KEY_LABEL)

        if (!label.isNullOrEmpty()) {
            binding.translate.text = label
            viewModel.getVideo(label!!)
        }

        BottomSheetBehavior.from(binding.bottom).apply {
            peekHeight = 150
            this.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        viewModel.isLoading.postValue(true)
        binding.apply {
            countDown.visibility = View.GONE
            backbtn.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
            getCameraPermission()
            if (permissionGranted) {
                cameraExecutor = Executors.newSingleThreadExecutor()
                startCamera()
            } else {
                Toast.makeText(applicationContext, "You have to grant permission", Toast.LENGTH_SHORT).show()
                finish()
            }
            photoLibrary.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*")
                startActivityForResult(intent, PICK_VIDEO)
            }

            recordbtn.setOnClickListener {
                if (recordState) {
                    recording?.stop()
                    recordState = false
                    recordbtn.setImageResource(R.drawable.recording1)
                } else {
                    if (modeRecording) {
                        binding.countDown.visibility = View.VISIBLE
                        viewModel.startTimer()
                        Handler().postDelayed(
                            {
                                initRecorder()
                                recordbtn.setImageResource(R.drawable.recording)
                                recordState = true
                                binding.countDown.visibility = View.GONE
                            },
                            3000,
                        )
                    } else {
                        initRecorder()
                        recordbtn.setImageResource(R.drawable.recording)
                        recordState = true
                    }
                }
            }
            manual.setOnClickListener {
                showManualDialog()
            }
            recordMode.setOnClickListener {
                if (!modeRecording) {
                    recordMode.setImageResource(R.drawable.group)
                    modeRecording = true
                } else {
                    recordMode.setImageResource(R.drawable.group_unclick)
                    modeRecording = false
                }
            }
        }
        viewModel.isLoading.postValue(false)
    }

    private fun showManualDialog() {
        val dialog = ManualDialogFragment()
        dialog.show(supportFragmentManager, "Test")
    }

    @OptIn(UnstableApi::class)
    private fun initRecorder() {
        val recordingListener =
            Consumer<VideoRecordEvent> { event ->
                when (event) {
                    is VideoRecordEvent.Start -> {
                        val msg = "Capture Started"
                        Toast
                            .makeText(this, msg, Toast.LENGTH_SHORT)
                            .show()
                    }
                    is VideoRecordEvent.Finalize -> {
                        val msg =
                            if (!event.hasError()) {
                                "Video capture succeeded"
                                    .also {
                                        currentVideoUri = event.outputResults.outputUri
                                        showVideoPreviewDialog(currentVideoUri)
                                    }
                            } else {
                                // update app state when the capture failed.
                                recording?.close()
                                recording = null

                                "Video capture ends with error: ${event.error}"
                            }
                        Toast
                            .makeText(this, msg, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        val contentValues =
            ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "${Date().time}")
                put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            }

        val mediaStoreOutputOptions =
            MediaStoreOutputOptions
                .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                .setContentValues(contentValues)
                .build()
        recording =
            videoCapture.output
                .prepareRecording(this, mediaStoreOutputOptions)
                .start(ContextCompat.getMainExecutor(this), recordingListener)
    }

    override fun initListener() {
    }

    override fun observerLiveData() {
        viewModel.apply {
            countdown.observe(this@PracticeCameraActivity) {
                binding.countDown.text = (it / 1000).toString()
            }
            videoRes.observe(this@PracticeCameraActivity) {
                if (!it?.body?.video_url.isNullOrEmpty()) {
                    it?.body?.video_url?.let { it -> initializePlayer(it) }
                    Log.d("VideoURL", it?.body?.video_url ?: "NULL")
                }
            }
            score.observe(this@PracticeCameraActivity) {
                if (it != null && it >= 0) {
                    val dialog = ResultDialog()
                    dialog.setScore(it)
                    dialog.show(supportFragmentManager, "Test")
                }
            }
        }
    }

    private fun initializePlayer(uri: String) {
        val player =
            ExoPlayer.Builder(this).build().also { exoPlayer ->
                binding.sampleVideo.player = exoPlayer
                val mediaItem: MediaItem = MediaItem.fromUri(uri)
                exoPlayer.setMediaItem(mediaItem)
            }
        player.prepare()
        player.play()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Preview
            val preview =
                Preview
                    .Builder()
                    .build()
                    .also {
                        it.setSurfaceProvider(binding.previewCamera.surfaceProvider)
                    }
            var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            if (currentSide_Camera == FRONT_CAMERA) {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
            }

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    videoCapture,
                    preview,
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun getCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                RealtimeDetectActivity.VIDEO_RECORD_CODE,
            )
        } else {
            permissionGranted = true
        }
    }

    companion object {
        private const val BACK_CAMERA = 1
        private const val FRONT_CAMERA = 0
        private const val PICK_VIDEO = 100
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_VIDEO) {
            when (resultCode) {
                RESULT_OK -> {
                    currentVideoUri = data?.data!!
                    showVideoPreviewDialog(currentVideoUri)
                }
                Activity.RESULT_CANCELED -> {
                    Log.d(TAG, "Cancel")
                }
                else -> {
                    Log.e(TAG, "Error")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun scoreLabel(uri: Uri) {
        viewModel.apply {
            predictVideo(File(parsePath(uri), ""), this@PracticeCameraActivity, label!!)
        }
    }

    private fun parsePath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor? =
            this.contentResolver.query(uri!!, projection, null, null, null)
        return if (cursor != null) {
            val columnIndex: Int =
                cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } else {
            null
        }
    }

    private fun showVideoPreviewDialog(videoUri: Uri) {
        val dialog = ConfirmationDialogFragment()
        dialog.setVideoUri(videoUri)
        dialog.show(supportFragmentManager, "VideoPreview")
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onKeepVideo() {
        viewModel.resultString.postValue(PracticeViewModel.AnalysisResult(currentVideoUri.path ?: "null"))
        scoreLabel(currentVideoUri)
    }

    override fun onDiscardVideo() {
        contentResolver.delete(currentVideoUri, null, null)
        Toast.makeText(this, "Video discarded", Toast.LENGTH_SHORT).show()
    }
}
