package com.ptit.signlanguage.network.model.response.VideoToText

data class Prediction(
    val action_id: Int,
    val action_name: String,
    val action_score: Double
)