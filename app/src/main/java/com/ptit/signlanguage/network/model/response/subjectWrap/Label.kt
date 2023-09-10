package com.ptit.signlanguage.network.model.response.subjectWrap

data class Label(
    val id: Int,
    val labelEn: String,
    val labelVn: String,
    val levelId: Int,
    val subjectId: Int
)