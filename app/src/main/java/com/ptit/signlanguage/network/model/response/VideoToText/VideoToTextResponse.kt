package com.ptit.signlanguage.network.model.response.VideoToText

import com.google.gson.annotations.SerializedName

data class VideoToTextResponse(
    val code: Int,
    val error: String,
    val prediction: String,
    val status: Boolean,
    val video_path: String,
    @SerializedName("accuracy")
    val score: Double,
)