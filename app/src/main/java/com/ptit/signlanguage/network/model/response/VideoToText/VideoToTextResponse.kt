package com.ptit.signlanguage.network.model.response.VideoToText

data class VideoToTextResponse(
    val code: Int,
    val error: String,
    val prediction: String,
    val status: Boolean,
    val video_path: String,
    val accuracy: Float
)