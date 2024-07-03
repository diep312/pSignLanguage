package com.ptit.signlanguage.network.model.request

data class UpdateScoreRequest(
    var labelId: Int,
    var score: Float,
)
