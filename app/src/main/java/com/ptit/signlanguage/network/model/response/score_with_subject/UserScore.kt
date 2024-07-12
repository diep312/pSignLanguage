package com.ptit.signlanguage.network.model.response.score_with_subject

import java.util.Date

data class UserScore(
    val userId: Int,
    val labelId : Int,
    val score: Int,
    val actionDate: Date
)
