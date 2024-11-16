package com.ptit.signlanguage.network.model.response.VideoToText

data class Prediction(
    val action_id: Int = 0,
    val prediction: String,
    val accuracy: Double
)