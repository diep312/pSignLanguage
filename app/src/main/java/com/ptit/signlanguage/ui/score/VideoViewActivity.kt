package com.ptit.signlanguage.ui.score

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.databinding.ActivityVideoViewBinding
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.view_model.ViewModelFactory

class VideoViewActivity : BaseActivity<MainViewModel, ActivityVideoViewBinding>() {
    private var label: String? = null

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }

    override fun getContentLayout(): Int {
        return R.layout.activity_video_view
    }

    override fun initView() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setLightIconStatusBar(false)
            setColorForStatusBar(R.color.color_primary)
            binding.layout.setPadding(0, getStatusBarHeight(this@VideoViewActivity), 0, 0)
        } else {
            setLightIconStatusBar(false)
            setColorForStatusBar(R.color.color_primary)
            binding.layout.setPadding(0, getStatusBarHeight(this@VideoViewActivity), 0, 0)
        }

        label = intent.getStringExtra(Constants.KEY_LABEL)
        if (!label.isNullOrEmpty()) {
            binding.tvWord.text = label
            viewModel.getVideo(label!!)
        }
    }

    override fun initListener() {
        binding.imvBack.setOnClickListener { finish() }
    }

    override fun observerLiveData() {
        viewModel.apply {
            videoRes.observe(this@VideoViewActivity) {
                if (!it?.body?.video_url.isNullOrEmpty()) {
                    it?.body?.video_url?.let { it1 -> initializePlayer(it1) }
                }
            }

            errorMessage.observe(this@VideoViewActivity) {
                Toast.makeText(binding.root.context, getString(it), Toast.LENGTH_LONG).show()
                Log.d(TAG, it.toString())
            }
        }
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