package com.ptit.signlanguage.ui.main.adapter

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.ptit.signlanguage.R
import com.ptit.signlanguage.databinding.ItemEmptyBinding
import com.ptit.signlanguage.databinding.ItemLabelBinding
import com.ptit.signlanguage.network.model.response.Label
import com.ptit.signlanguage.ui.score.VideoViewActivity
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.Constants.KEY_LABEL
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext

class LabelAdapter(var listLabel: MutableList<Label?>, val language: String, val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_LABEL = 0
        const val Type_HIDE = 1
    }

    private val scope = CoroutineScope(Dispatchers.IO) + CoroutineExceptionHandler { _, throwable ->
        Log.e("Error", throwable.message.toString())
    }

    fun replace(listLabel: MutableList<Label?>) {
        this.listLabel = listLabel
        notifyItemRangeChanged(0, itemCount)
    }

    var getVideoCallback: ((String) -> Flow<String>)? = null

    inner class LabelViewHolder(var binding: ItemLabelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(label: Label?) {

            var player : ExoPlayer? = null
            binding.expandedView.visibility = View.GONE
            binding.nextBtn.visibility = View.GONE
            binding.btnNext.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color_secondary))
            binding.tvLabel.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue_button_filled)))
            if (language == Constants.EN) {
                binding.tvLabel.text = label?.labelEn ?: Constants.EMPTY_STRING
            } else {
                binding.tvLabel.text = label?.labelVn ?: Constants.EMPTY_STRING
            }
            binding.btnNext.setOnClickListener {
                if (!label!!.isExpanded) {
                    label.isExpanded = true
                    binding.expandedView.visibility = View.VISIBLE
                    binding.btnNext.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color_primary))
                    binding.tvLabel.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color_secondary)))
                    val prefixURL = "http://ptitsignlanguage.edu.vn"
                    scope.launch {
                        getVideoCallback?.invoke(label.labelVn ?: Constants.EMPTY_STRING)
                            ?.onEach {
                                val videoPath = it.replace(prefixURL, "http://113.22.56.109:5005", true)
                                withContext(Dispatchers.Main){
                                    player =
                                        ExoPlayer.Builder(context).build().also { exoPlayer ->
                                            binding.vvGuide.player = exoPlayer
                                            val mediaItem: MediaItem = MediaItem.fromUri(videoPath)
                                            exoPlayer.setMediaItem(mediaItem)
                                            exoPlayer.volume = 0f
                                        }

                                    player!!.prepare()
                                    player!!.addListener(object : Player.Listener{
                                        override fun onPlaybackStateChanged(playbackState: Int) {
                                            super.onPlaybackStateChanged(playbackState)
                                            if(playbackState == Player.STATE_READY){
                                                binding.animVideoLoader.visibility = View.GONE
                                                player!!.play()
                                            }
                                            if(playbackState == Player.STATE_ENDED){
                                                Handler(Looper.getMainLooper()).postDelayed({
                                                    player!!.seekTo(0)
                                                    player!!.play()
                                                },1000)
                                            }
                                        }
                                    })
                                }
                            }?.launchIn(scope)
                    }

                    binding.nextBtn.apply {
                        visibility = View.VISIBLE
                        setOnClickListener {
                            val intent = Intent(binding.root.context, VideoViewActivity::class.java)
                            intent.putExtra("Language", language)
                            intent.putExtra(Constants.KEY_LABEL, label.labelVn)
                            if (language == Constants.EN) {
                                intent.putExtra("fix", label.labelEn)
                            } else {
                                intent.putExtra("fix", label.labelVn)
                            }
                            binding.root.context.startActivity(intent)
                        }
                    }
                } else {
                    player?.release()
                    label.isExpanded = false
                    binding.expandedView.visibility = View.GONE
                    binding.nextBtn.visibility = View.GONE
                    binding.btnNext.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color_secondary))
                    binding.tvLabel.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blue_button_filled)))
                }
            }
        }
    }

    class EmptyViewHolder(var binding: ItemEmptyBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_LABEL) {
            val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
            val item = DataBindingUtil.inflate<ItemLabelBinding>(
                layoutInflater,
                R.layout.item_label,
                parent,
                false
            )
            return LabelViewHolder(item)
        } else {
            val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
            val item = DataBindingUtil.inflate<ItemEmptyBinding>(
                layoutInflater,
                R.layout.item_empty,
                parent,
                false
            )
            return EmptyViewHolder(item)
        }
    }

    override fun getItemCount(): Int {
        return listLabel.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_LABEL) {
            (holder as LabelViewHolder).bind(listLabel[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listLabel[position]?.isShow == true) {
            TYPE_LABEL
        } else {
            Type_HIDE
        }
    }

}