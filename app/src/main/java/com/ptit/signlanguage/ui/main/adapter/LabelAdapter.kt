package com.ptit.signlanguage.ui.main.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
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
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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
            binding.btnNext.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.color_secondary))
            binding.tvLabel.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)))

            if (language == Constants.EN) {
                binding.tvLabel.text = label?.labelEn ?: Constants.EMPTY_STRING
            } else {
                binding.tvLabel.text = label?.labelVn ?: Constants.EMPTY_STRING
            }

            binding.btnNext.setOnClickListener {
                if (!label!!.isExpanded) {
                    label.isExpanded = true

                    val expandAnimator = ObjectAnimator.ofFloat(binding.expandedView, "scaleY", 0f, 1f).apply {
                        duration = 300
                        interpolator = DecelerateInterpolator()
                        binding.expandedView.visibility = View.VISIBLE
                    }

                    val rotateAnimator = ObjectAnimator.ofFloat(binding.nextBtn, "rotation", 90f, 0f).apply {
                        duration = 300
                        interpolator = DecelerateInterpolator()
                    }


                    val fadeAnimator = ObjectAnimator.ofFloat(binding.vvGuide, "alpha", 0f, 1f).apply {
                        duration = 300
                    }

                    AnimatorSet().apply {
                        playTogether(expandAnimator, rotateAnimator, fadeAnimator)
                        start()
                    }

                    binding.tvLabel.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primary)))

                    val prefixURL = "http://ptitsignlanguage.edu.vn"
                    scope.launch {
                        getVideoCallback?.invoke(label.labelVn ?: Constants.EMPTY_STRING)?.collect {
                            withContext(Dispatchers.Main) {
                                val videoPath = it.replace(prefixURL, "http://113.22.56.109:5005", true)
                                player = ExoPlayer.Builder(context).build().also { exoPlayer ->
                                    binding.vvGuide.player = exoPlayer
                                    val mediaItem: MediaItem = MediaItem.fromUri(videoPath)
                                    exoPlayer.setMediaItem(mediaItem)
                                    exoPlayer.volume = 0f
                                }

                                player!!.prepare()
                                player!!.addListener(object : Player.Listener {
                                    override fun onPlaybackStateChanged(playbackState: Int) {
                                        super.onPlaybackStateChanged(playbackState)
                                        if (playbackState == Player.STATE_READY) {
                                            binding.animVideoLoader.visibility = View.GONE
                                            player!!.play()
                                        }
                                        if (playbackState == Player.STATE_ENDED) {
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                player!!.seekTo(0)
                                                player!!.play()
                                            }, 1000)
                                        }
                                    }
                                })
                            }
                        }
                    }

                    binding.nextBtn.apply {
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

                    val collapseAnimator = ObjectAnimator.ofFloat(binding.expandedView, "scaleY", 1f, 0f).apply {
                        duration = 300
                        interpolator = DecelerateInterpolator()
                    }

                    val rotateBackAnimator = ObjectAnimator.ofFloat(binding.nextBtn, "rotation", 0f, 90f).apply {
                        duration = 300
                        interpolator = DecelerateInterpolator()
                    }

                    AnimatorSet().apply {
                        playTogether(collapseAnimator, rotateBackAnimator)
                        start()
                        doOnEnd {
                            binding.expandedView.visibility = View.GONE
                        }
                    }

                    binding.tvLabel.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.black)))
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