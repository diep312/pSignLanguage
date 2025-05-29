package com.ptit.signlanguage.ui.predict

import android.app.Dialog
import android.content.Context
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.VideoView
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseDialogFragment

class ConfirmationDialogFragment : BaseDialogFragment() {

    interface VideoPreviewListener {
        fun onKeepVideo()
        fun onDiscardVideo()
    }

    private var listener: VideoPreviewListener? = null
    private lateinit var videoUri: Uri


    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? VideoPreviewListener
        if (listener == null) {
            throw ClassCastException("$context must implement VideoPreviewListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_confirmation)

        val textureView = dialog.findViewById<TextureView>(R.id.texture_video_preview)
        val mediaPlayer = MediaPlayer()
        val btnKeep = dialog.findViewById<ImageView>(R.id.btn_check)
        val btnDiscard = dialog.findViewById<ImageView>(R.id.btn_discard)

        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                val surface = Surface(surface)
                mediaPlayer.setSurface(surface)
                mediaPlayer.setDataSource(requireContext(), videoUri)
                mediaPlayer.isLooping = true
                mediaPlayer.setOnPreparedListener { mp ->
                    mp.start()
                }
                mediaPlayer.prepareAsync()
            }

            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean = true
            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }
        btnKeep.setOnClickListener {
            listener?.onKeepVideo()
            dismiss()
        }

        btnDiscard.setOnClickListener {
            listener?.onDiscardVideo()
            dismiss()
        }


        return dialog
    }

    fun setVideoUri(uri: Uri) {
        videoUri = uri
    }
    private fun setVideoViewToLoop(videoView : VideoView) {
        videoView.setOnCompletionListener {
            videoView.start()
        }
    }

}
