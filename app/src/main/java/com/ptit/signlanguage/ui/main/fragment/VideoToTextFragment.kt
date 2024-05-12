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
import android.view.View
import android.widget.MediaController
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseFragment
import com.ptit.signlanguage.data.prefs.PreferencesHelper
import com.ptit.signlanguage.databinding.FragmentVideoToTextBinding
import com.ptit.signlanguage.network.model.response.User
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.Constants.EN
import com.ptit.signlanguage.utils.GsonUtils
import com.ptit.signlanguage.view_model.ViewModelFactory
import java.io.File


class VideoToTextFragment : BaseFragment<MainViewModel, FragmentVideoToTextBinding>() {

    var user : User? = null
    private lateinit var prefsHelper: PreferencesHelper

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.fragment_video_to_text
    }

    override fun observerLiveData() {
        viewModel.apply {
            videoToTextRes.observe(this@VideoToTextFragment) {
                if (it != null) {
                    binding.layoutWrapAnswer.visibility = View.VISIBLE
                    binding.vvVideo.visibility = View.VISIBLE
                    binding.ivIllu.visibility = View.INVISIBLE
                    binding.tvTranslatedesc.visibility = View.INVISIBLE
                    binding.btnRecord.text = getString(R.string.str_again)

                    if(user?.language.equals(EN)) {
                        binding.tvLabel.text = getString(R.string.str_label, it.prediction[0].action_name)
                    } else {
                        binding.tvLabel.text = getString(R.string.str_label, it.prediction[0].action_name)
                    }
                }
                else{
                    binding.ivIllu.visibility = View.VISIBLE
                }
                Log.d(TAG, it.toString())
            }
            errorMessage.observe(this@VideoToTextFragment) {
                Log.d(TAG, it.toString())
            }
        }
    }

    override fun initView() {
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.vvVideo)
        mediaController.setMediaPlayer(binding.vvVideo)
        binding.vvVideo.setMediaController(mediaController)

        binding.vvVideo.visibility = View.INVISIBLE
        prefsHelper = PreferencesHelper(binding.root.context)
        val userJson = prefsHelper.getString(Constants.KEY_PREF_DATA_LOGIN)
        user = GsonUtils.deserialize(userJson, User::class.java)
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

                    val file = File(videoPath)
                    if (file != null) {
                        binding.layoutWrapAnswer.visibility = View.GONE
                        viewModel.videoToText(file)
                    }
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

                    val file = File(videoPath)
                    if (file != null) {
                        binding.layoutWrapAnswer.visibility = View.GONE
                        viewModel.videoToText(file)
                    }
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
            requireActivity().contentResolver.query(uri!!, projection, null, null, null)
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