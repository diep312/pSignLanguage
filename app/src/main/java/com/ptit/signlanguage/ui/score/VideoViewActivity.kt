package com.ptit.signlanguage.ui.score

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.databinding.AcivityVideoViewBinding
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.view_model.ViewModelFactory
import java.util.concurrent.TimeUnit

class VideoViewActivity : BaseActivity<MainViewModel, AcivityVideoViewBinding>() {
    private var label: String? = null
    private var mediaPlayer: MediaPlayer? = null

    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }
    override fun getContentLayout(): Int {
        return R.layout.acivity_video_view
    }
    override fun initView() {
        setLightIconStatusBar(true)
        setColorForStatusBar(R.color.color_bg)

        binding.layout.setPadding(0, getStatusBarHeight(this@VideoViewActivity), 0, 0)
        label = intent.getStringExtra(Constants.KEY_LABEL)

        if (!label.isNullOrEmpty()) {
            binding.tvWord.text = intent.getStringExtra("fix")
            viewModel.getVideo(label!!)
        }
//
//        binding.tvTotaltime.text = convertoMMSS(binding.vvGuide.player!!.duration.toString())

    }

    override fun initListener() {
        binding.imvBack.setOnClickListener { finish() }
        binding.btnNext.setOnClickListener{}
        binding.btnPrev.setOnClickListener{}
    }

    override fun observerLiveData() {
        viewModel.apply {
            videoRes.observe(this@VideoViewActivity) {
                if (!it?.body?.video_url.isNullOrEmpty()) {
                    it?.body?.video_url?.let { it1 -> initializePlayer(it1) }
                    Log.d(TAG, it!!.body!!.video_url!!.toString())
                }
            }
            errorMessage.observe(this@VideoViewActivity) {
                Toast.makeText(binding.root.context, getString(it), Toast.LENGTH_LONG).show()
                Log.d(TAG, it.toString())
            }
        }
    }

    private fun initializePlayer(uri: String) {
        val player: ExoPlayer = ExoPlayer.Builder(this).build().also{
            exoPlayer -> binding.vvGuide.player = exoPlayer
            val mediaItem: MediaItem = MediaItem.fromUri(uri)
            exoPlayer.setMediaItem(mediaItem)
        }

        player.play()
    }

    private fun convertoMMSS(duration: String): String{
        val milisecs = duration.toLong()
        return String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(milisecs) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(milisecs) % TimeUnit.HOURS.toSeconds(1)
        )
    }
}