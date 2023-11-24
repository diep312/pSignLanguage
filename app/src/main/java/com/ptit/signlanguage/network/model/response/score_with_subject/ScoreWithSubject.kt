package com.ptit.signlanguage.network.model.response.score_with_subject

data class ScoreWithSubject(
    val message: String,
    val scoreList: List<Score>,
    val status: Boolean
)