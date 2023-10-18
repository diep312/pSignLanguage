package com.ptit.signlanguage.network.model.response.score_with_subject

data class Score(
    val scoreAverage: Double,
    val totalLabel: Int,
    val totalScore: Double,
    val userId: Int,
    val userName: String
)