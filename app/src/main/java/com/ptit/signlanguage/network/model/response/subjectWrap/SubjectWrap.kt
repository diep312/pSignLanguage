package com.ptit.signlanguage.network.model.response.subjectWrap

data class SubjectWrap(
    val listLevel: List<Level?>,
    val subjectId: String,
    val subjectName: String,
    val total: Int
)