package com.ptit.signlanguage.network.model.response

data class VideoToTextResponse(
    val action_code: Int,
    val action_en: String,
    val action_vi: String,
    val code: Int,
    val error: String,
    val status: Boolean,
    val video_path: String
)