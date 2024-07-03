package com.ptit.signlanguage.network.model.response.subjectWrap

data class Level(
    val levelId: Int,
    val listLabel: List<LabelWithScore>
) : java.io.Serializable