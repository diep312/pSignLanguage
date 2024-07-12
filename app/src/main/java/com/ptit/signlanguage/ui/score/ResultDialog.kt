package com.ptit.signlanguage.ui.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.ptit.signlanguage.R
import com.ptit.signlanguage.base.BaseDialogFragment

class ResultDialog : BaseDialogFragment() {
    private var score: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.score_dialog, container)
        val sentence = view.findViewById<TextView>(R.id.sentence)
        val scoreText = view.findViewById<TextView>(R.id.score)
        when {
            score < 50 -> {
                scoreText.text = "%d/100".format(score)
                sentence.text = "Try Again"
            }
            score in 50..80 -> {
                scoreText.text = "%d/100".format(score)
                sentence.text = "Good, but needs improvement."
            }
            score in 81..100 -> {
                scoreText.text = "%d/100".format(score)
                sentence.text = "Perfect!"
            }
        }
        return view
    }

    fun setScore(score: Int) {
        this.score = score
    }

}
