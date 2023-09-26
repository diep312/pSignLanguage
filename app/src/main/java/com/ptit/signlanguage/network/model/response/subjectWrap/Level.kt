package com.ptit.signlanguage.network.model.response.subjectWrap

data class Level(
    val levelId: Int,
    val listLabel: List<Label>
) : java.io.Serializable