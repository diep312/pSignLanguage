package com.ptit.signlanguage.network.model.response.subjectWrap
data class LabelWithScore(
    val id: Int,
    val labelEn: String,
    val labelVn: String,
    val levelId: Int,
    val subjectId: Int,
    val latestScore: Float?,
) : java.io.Serializable