package com.ptit.signlanguage.ui.score
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
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
    }

    override fun initListener() {
        binding.imvBack.setOnClickListener { finish() }
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
        val seekBar = binding.seekBar;
        val currentTimeTv = binding.tvCurtime;
        val pausePlay = binding.btnPlay;
        val player: ExoPlayer = ExoPlayer.Builder(this).build().also{
            exoPlayer -> binding.vvGuide.player = exoPlayer
            val mediaItem: MediaItem = MediaItem.fromUri(uri)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.playWhenReady = true
        }
        player.prepare()
        player.play()


        binding.btnPlay.setOnClickListener(){
            if(player.isPlaying){
                player.pause()
                pausePlay.setImageResource(R.drawable.ic_pause)
            }
            else{
                player.play()
                pausePlay.setImageResource(R.drawable.ic_play)
            }
        }

        this@VideoViewActivity.runOnUiThread(object : Runnable {
            override fun run() {
                //Seekbar progress là dạng int từ 0 - 100% -> cần chuyển position theo tỷ lệ duration của toàn bộ vid
                binding.tvTotaltime.setText(convertToMMSS(player.duration))
                seekBar.setProgress(((player.currentPosition.toFloat() / player.duration.toFloat()) * 100).toInt())
                currentTimeTv.setText(convertToMMSS(player.currentPosition))
                Handler().postDelayed(this, 100)
            }
        })
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    player.seekTo((player.duration.toFloat() * (progress.toFloat() / 100)).toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

    }

    private fun convertToMMSS(milisecs: Long): String{
        return String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(milisecs) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(milisecs) % TimeUnit.HOURS.toSeconds(1)
        )
    }
}