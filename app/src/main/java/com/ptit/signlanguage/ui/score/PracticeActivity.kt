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
import android.widget.MediaController
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
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
    private var videoPath : String? = null

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_practice
    }

    override fun initView() {
        setLightIconStatusBar(false)
        setColorForStatusBar(R.color.color_primary)
        binding.layout.setPadding(0, getStatusBarHeight(this@PracticeActivity), 0, 0)

        label = intent.getStringExtra(Constants.KEY_LABEL)
        if (!label.isNullOrEmpty()) {
            binding.tvWord.text = label
            viewModel.getVideo(label!!)
        }
    }

    override fun initListener() {
        binding.imvRecord.setOnClickListener {
            if (!isDoubleClick()) {
                if (checkCamera()) {
                    getCameraPermission()
                }
            }
        }

        binding.imvBack.setOnClickListener { finish() }
        binding.btnCheck.setOnClickListener {
            binding.imvCheck.visibility = View.GONE
            if(videoPath.isNullOrEmpty()) {
                Toast.makeText(binding.root.context, "Bạn chưa chọn video!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val file = File(videoPath)
            if (file != null) {
//                binding.layoutWrapAnswer.visibility = View.GONE
                viewModel.videoToText(file)
            }
        }
    }

    override fun observerLiveData() {
        viewModel.apply {
            videoRes.observe(this@PracticeActivity) {
                if (!it?.body?.video_url.isNullOrEmpty()) {
                    val uri = Uri.parse(it?.body?.video_url)
//                    val headers: MutableMap<String, String> = HashMap()
//                    headers["Content-Type"] = "video/mp4" // change content type if necessary
//                    headers["Accept-Ranges"] = "bytes"
//                    headers["Status"] = "206"
//                    headers["Cache-control"] = "no-cache"
                    binding.vvGuide.setVideoURI(uri)
                    val mediaController = MediaController(binding.root.context)
                    mediaController.setAnchorView(binding.vvGuide)
                    mediaController.setMediaPlayer(binding.vvGuide)
                    binding.vvGuide.setMediaController(mediaController)
                    binding.vvGuide.requestFocus()
                    binding.vvGuide.start()
                }
            }

            videoToTextRes.observe(this@PracticeActivity) {
                if (it?.body != null) {
                    binding.imvCheck.visibility = View.VISIBLE
                    if(label?.lowercase()?.equals(it.body.action_vi.lowercase()) == true) {
                        binding.imvCheck.setImageResource(R.drawable.ic_check_true)
                    } else {
                        binding.imvCheck.setImageResource(R.drawable.ic_check_close)
                    }
                }
                Log.d(TAG, it.toString())
            }
            errorMessage.observe(this@PracticeActivity) {
                binding.imvCheck.visibility = View.VISIBLE
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

    //    fun playVideo(url : String) {
//        val uri = Uri.parse(url)
//        binding.vvGuide.setVideoURI(uri)
//        binding.vvGuide.setMediaController(MediaController(this))
//
//        binding.vvGuide.setOnErrorListener { mp, what, extra ->
//            false
//        }
//
//
//    }
    companion object {
        private const val CAMERA_PERMISSION_CODE: Int = 100
        private const val VIDEO_RECORD_CODE: Int = 101
    }


}