package com.ptit.signlanguage.ui.score
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.intl.Locale
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseActivity
import com.ptit.signlanguage.databinding.AcivityVideoViewBinding
import com.ptit.signlanguage.ui.main.MainViewModel
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.view_model.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit


class VideoViewActivity : BaseActivity<MainViewModel, AcivityVideoViewBinding>() {
    private var label: String? = null
    private var language: String? = Constants.EN
    private var player: ExoPlayer? = null
    private var playerState = MutableLiveData(false)
    private var replayState = true
    private var isBookmarked = false
    private var responseGemini = ""
    override fun initViewModel() {
        viewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
    }
    override fun getContentLayout(): Int {
        return R.layout.acivity_video_view
    }
    override fun initView() {
        setLightIconStatusBar(true)
        setColorForStatusBar(R.color.color_bg)
        btnPlay()
        binding.layout.setPadding(0, getStatusBarHeight(this@VideoViewActivity), 0, 0)
        label = intent.getStringExtra(Constants.KEY_LABEL)
        language = intent.getStringExtra("Language")

        if (!label.isNullOrEmpty()) {
            binding.tvWord.text = intent.getStringExtra("fix")
            viewModel.getVideo(label!!)
        }

        binding.bookMark.setOnClickListener(){
            if(!isBookmarked){
                binding.bookMark.setImageResource(R.drawable.book_marked_24px)
                isBookmarked = true
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            }else{
                binding.bookMark.setImageResource(R.drawable.book_24px)
                Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show()
                isBookmarked = false
            }
        }
        binding.genDef.setOnClickListener {
            binding.genDef.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this,R.color.color_highlight))
            if(!TextUtils.isEmpty(responseGemini)){
                binding.tvGenText.text = responseGemini
            }else{
                lifecycleScope.launch {
                    viewModel.getGeminiResponse(language!!, intent.getStringExtra("fix")!!)
                        .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                        .collect{
                            responseGemini += it
                            binding.tvGenText.text = responseGemini
                    }
                }
            }
        }
    }
    override fun initListener() {
        binding.imvBack.setOnClickListener { finish() }
    }
    override fun observerLiveData() {
        playerState.observe(this@VideoViewActivity){
            if(it){
                binding.btnPlay.setImageResource(R.drawable.ic_pause)
            }else{
                binding.btnPlay.setImageResource(R.drawable.ic_play)
            }
        }
        viewModel.apply {
            videoRes.observe(this@VideoViewActivity) {
                if (!it?.body?.video_url.isNullOrEmpty()) {
                    it?.body?.video_url?.let { it1 -> initializePlayer(it1)}
                    Log.d(TAG, it!!.body!!.video_url)
                }
                else{
                    Toast.makeText(binding.root.context, R.string.video_not_available, Toast.LENGTH_SHORT).show()
                }
            }
            errorMessage.observe(this@VideoViewActivity) {
                Toast.makeText(binding.root.context, getString(it), Toast.LENGTH_LONG).show()
                Log.d(TAG, it.toString())
            }
        }

    }
    private fun btnPlay(){
        binding.btnPlay.setOnClickListener(){
            if(player != null){
                if(player!!.isPlaying){
                    player!!.pause()
                    playerState.postValue(false)
                }
                else{
                    player!!.play()
                    playerState.postValue(true)
                }
            }
            else{
                Toast.makeText(this, "Video not found", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
        }
        binding.btnReplay.setOnClickListener(){
            if(replayState){
                replayState = false
                binding.btnReplay.setImageResource(R.drawable.ic_replay)
            }else{
                replayState = true
                binding.btnReplay.setImageResource(R.drawable.ic_replay_clicked)
            }
        }
    }
    @Synchronized
    private fun initializePlayer(uri: String) {

        val prefixURL = "http://ptitsignlanguage.edu.vn"
        var videoPath = uri.replace(prefixURL,"http://113.22.56.109:5005",true)
        Log.d("TAG", videoPath)
        val seekBar = binding.seekBar
        player = ExoPlayer.Builder(this).build().also{  exoPlayer ->
            binding.vvGuide.player = exoPlayer
            val mediaItem: MediaItem = MediaItem.fromUri(videoPath)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.volume = 0f
        }
        player!!.prepare()
        playerState.postValue(false)
        btnPlay()
        player!!.addListener(object : Player.Listener{
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if(playbackState == Player.STATE_READY){
                    updateRunTime()
                    binding.animVideoLoader.visibility = View.GONE
                    binding.tvTotaltime.text = convertToMMSS(player!!.duration)
                    seekBar.max = (player!!.duration / 1000).toInt()
                    player!!.play().also {
                        playerState.postValue(true)
                    }
                }
                if(playbackState == Player.STATE_ENDED){
                    if(replayState){
                        Handler(Looper.getMainLooper()).postDelayed({
                            player!!.seekTo(0)
                            player!!.play()
                        },1000)
                    }else{
                        player!!.seekBack()
                        player!!.pause()
                        playerState.postValue(false)
                    }
                }
            }
        })
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    player!!.seekTo((progress.toFloat()*1000).toLong())
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

    }

    private fun convertToMMSS(milisecs: Long): String{
        return String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(milisecs) % TimeUnit.HOURS.toMinutes(1),
            TimeUnit.MILLISECONDS.toSeconds(milisecs) % TimeUnit.HOURS.toSeconds(1)
        )
    }
    fun updateRunTime(){
        this@VideoViewActivity.runOnUiThread(object : Runnable {
            override fun run() {
                binding.seekBar.progress = ((player?.currentPosition ?: 0) / 1000).toInt()
                binding.tvCurtime.text = player?.let { convertToMMSS(it.currentPosition) }
                Handler().postDelayed(this, 100)
            }
        })
    }
    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}