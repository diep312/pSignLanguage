package com.ptit.signlanguage.ui.score

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.data.prefs.PreferencesHelper
import com.ptit.signlanguage.databinding.ActivityPracticeBinding
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.view_model.ViewModelFactory
import java.io.File


class PracticeActivity : BaseActivity<MainViewModel, ActivityPracticeBinding>() {
    private var label: String? = null
    private lateinit var prefsHelper: PreferencesHelper
    private var videoPath: String? = null



    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_practice
    }


    override fun initView() {
        setLightIconStatusBar(true)
        setColorForStatusBar(R.color.color_bg)
        binding.layout.setPadding(0, getStatusBarHeight(this@PracticeActivity), 0, 0)
        var labelVn = intent.getStringExtra("fix")
        label = intent.getStringExtra(Constants.KEY_LABEL)

        if (!label.isNullOrEmpty()) {
            binding.tvWord.text = label
            viewModel.getVideo(labelVn!!)
        }

        BottomSheetBehavior.from(binding.bottom).apply{
            peekHeight=300
            this.state=BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun initListener() {
        binding.imvRecord.setOnClickListener {
            if (!isDoubleClick()) {
                binding.imvCheck.visibility = View.GONE
                binding.tvScore.visibility = View.GONE
                if (checkCamera()) {
                    getCameraPermission()
                }
            }
        }

        binding.imvBack.setOnClickListener { finish() }
        binding.btnCheck.setOnClickListener {
            binding.imvCheck.visibility = View.GONE
            binding.tvScore.visibility = View.GONE
            if (videoPath.isNullOrEmpty()) {
                Toast.makeText(
                    binding.root.context,
                    getString(R.string.str_you_did_not_choose_video),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            val file = File(videoPath)
            if (file != null) {
                binding.imVidview.visibility = View.GONE
                label?.let { it1 -> viewModel.checkVideo(file, it1) }
            }
        }
        binding.imvPick.setOnClickListener {
            if (!isDoubleClick()) {
                binding.imvCheck.visibility = View.GONE
                binding.tvScore.visibility = View.GONE
                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*")
                startActivityForResult(intent, REQUEST_TAKE_GALLERY_VIDEO)
            }
        }
    }

    override fun observerLiveData() {
        viewModel.apply {
            videoRes.observe(this@PracticeActivity) {
                if (!it?.body?.video_url.isNullOrEmpty()) {
                    it?.body?.video_url?.let { it -> initializePlayer(it) }
                }
            }

            checkVideoRes.observe(this@PracticeActivity) {
                if (it != null) {
                    binding.imvCheck.visibility = View.VISIBLE
                    binding.tvScore.visibility = View.VISIBLE

                    if (it.prediction[0].action_name == label) {
                        binding.tvScore.text =  "Score: " + "${it.prediction[0].action_score * 10} / 10"
                        binding.imvCheck.setImageResource(R.drawable.ic_check_true)
                    } else {
                        binding.tvScore.text =  "Score: " + "0 / 10"
                        binding.imvCheck.setImageResource(R.drawable.ic_check_close)
                    }
                }
                Log.d(TAG, it.toString())
            }

            errorMessage.observe(this@PracticeActivity) {
                binding.imvCheck.visibility = View.GONE
                binding.tvScore.visibility = View.GONE
                Toast.makeText(binding.root.context, getString(it), Toast.LENGTH_LONG).show()
                Log.d(TAG, it.toString())
            }
        }
    }

    private fun checkCamera(): Boolean {
        return binding.root.context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    private fun getCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                binding.root.context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            recordVideo()
        }
    }

    private fun recordVideo() {
        binding.imvCheck.visibility = View.GONE
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_RECORD_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VIDEO_RECORD_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    val videoUri = data?.data
                    videoPath = parsePath(videoUri)
                    Log.d(TAG, "$videoPath is the path that you need...")
                    binding.vvRecord.setVideoPath(videoPath)
                    binding.vvRecord.start()
                }
                Activity.RESULT_CANCELED -> {
                    Log.d(TAG, "Cancel")
                }
                else -> {
                    Log.e(TAG, "Error")
                }
            }
        } else if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
            when (resultCode) {
                RESULT_OK -> {
                    val videoUri: Uri = data?.data!!
                    videoPath = parsePath(videoUri)
                    Log.d(TAG, "$videoPath is the path that you need...")
                    binding.vvRecord.setVideoPath(videoPath)
                    binding.vvRecord.start()
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

    private fun parsePath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor? =
            binding.root.context.contentResolver.query(uri!!, projection, null, null, null)
        return if (cursor != null) {
            val columnIndex: Int = cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            val path = cursor.getString(columnIndex)
            cursor.close()
            return path
        } else null
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE: Int = 100
        private const val VIDEO_RECORD_CODE: Int = 101
        private const val REQUEST_TAKE_GALLERY_VIDEO: Int = 102
    }

    private fun initializePlayer(uri: String) {
        val mediaItem: MediaItem = MediaItem.Builder()
            .setUri(uri)
            .setMimeType(MimeTypes.VIDEO_MP4) // Change the MIME type as needed
            .build()
        val player: ExoPlayer = ExoPlayer.Builder(this).build()
        binding.vvGuide.player = player
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }


}