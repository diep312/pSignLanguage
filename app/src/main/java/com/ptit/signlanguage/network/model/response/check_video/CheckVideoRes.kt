package com.ptit.signlanguage.network.model.response.check_video

data class CheckVideoRes(
    val actionName: String,
    val actionNameEn: String,
    val message: String,
    val score: Double,
    val status: Boolean,
    val videoPath: String
)