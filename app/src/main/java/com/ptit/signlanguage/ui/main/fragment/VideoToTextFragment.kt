package com.ptit.signlanguage.ui.main.fragment

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.MediaController
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseFragment
import com.ptit.signlanguage.databinding.FragmentVideoToTextBinding
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.view_model.ViewModelFactory


class VideoToTextFragment : BaseFragment<MainViewModel, FragmentVideoToTextBinding>() {

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.fragment_video_to_text
    }

    override fun observerLiveData() {
        viewModel.apply {

        }
    }

    override fun initView() {
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.vvVideo)
        binding.vvVideo.setMediaController(mediaController)
    }

    override fun initListener() {
        binding.btnRecord.setOnClickListener {
            if (checkCamera()) {
                getCameraPermission()
            }
        }
        binding.btnPickVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*")
            startActivityForResult(intent, REQUEST_TAKE_GALLERY_VIDEO)
        }
    }

    private fun recordVideo() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_RECORD_CODE)
    }

    private fun checkCamera(): Boolean {
        return requireActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
    }

    private fun getCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            recordVideo()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VIDEO_RECORD_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    val videoUri = data?.data
                    val videoPath = parsePath(videoUri)
                    Log.d(TAG, "$videoPath is the path that you need...")
                    binding.vvVideo.setVideoPath(videoPath)
                    binding.vvVideo.start()
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
                    val videoPath = parsePath(videoUri)
                    Log.d(TAG, "$videoPath is the path that you need...")
                    binding.vvVideo.setVideoPath(videoPath)
                    binding.vvVideo.start()
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

    fun parsePath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor: Cursor? = requireActivity().contentResolver.query(uri!!, projection, null, null, null)
        return if (cursor != null) {
            val columnIndex: Int = cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } else null
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE: Int = 100
        private const val VIDEO_RECORD_CODE: Int = 101
        private const val REQUEST_TAKE_GALLERY_VIDEO: Int = 102
    }
}