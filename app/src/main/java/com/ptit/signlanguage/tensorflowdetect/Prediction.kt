package com.ptit.signlanguage.ui.tensorflowdetect

data class Prediction(
    val label: String,
    val score: Float,
) {
    override fun toString(): String =
        "$label " +
            "\nScore: $score"
}
