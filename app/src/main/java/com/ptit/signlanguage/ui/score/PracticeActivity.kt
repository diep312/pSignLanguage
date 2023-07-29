package com.ptit.signlanguage.ui.score

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.databinding.ActivityPracticeBinding
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.view_model.ViewModelFactory

class PracticeActivity : BaseActivity<MainViewModel, ActivityPracticeBinding>() {

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

    }

    override fun initListener() {
        binding.imvRecord.setOnClickListener {
            if(!isDoubleClick()) {
                if (checkCamera()) {
                    getCameraPermission()
                }
            }
        }

        binding.imvBack.setOnClickListener { finish() }
        binding.btnBack.setOnClickListener { finish() }
    }

    override fun observerLiveData() {

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
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_RECORD_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VIDEO_RECORD_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    val videoUri = data?.data
                    val videoPath = parsePath(videoUri)
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
    }
}