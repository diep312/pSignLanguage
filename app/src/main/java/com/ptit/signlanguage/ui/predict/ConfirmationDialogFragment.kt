package com.ptit.signlanguage.ui.predict

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.VideoView
import androidx.fragment.app.DialogFragment
import com.ptit.signlanguage.R

class ConfirmationDialogFragment : DialogFragment() {

    interface VideoPreviewListener {
        fun onKeepVideo()
        fun onDiscardVideo()
    }

    private var listener: VideoPreviewListener? = null
    private lateinit var videoUri: Uri

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

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

        val videoView = dialog.findViewById<VideoView>(R.id.vw_preview_vid)
        val btnKeep = dialog.findViewById<ImageView>(R.id.btn_check)
        val btnDiscard = dialog.findViewById<ImageView>(R.id.btn_discard)

        videoView.setVideoURI(videoUri)
        videoView.start()
        setVideoViewToLoop(videoView)

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
