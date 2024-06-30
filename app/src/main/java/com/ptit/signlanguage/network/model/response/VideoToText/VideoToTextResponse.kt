package com.ptit.signlanguage.network.model.response.VideoToText

data class VideoToTextResponse(
    val code: Int,
    val error: String,
    val prediction: List<Prediction>,
    val status: Boolean,
    val video_path: String
)