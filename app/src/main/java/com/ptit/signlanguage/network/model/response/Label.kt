package com.ptit.signlanguage.network.model.response

class Label(
    val id: Int? = null,
    val labelEn: String? = null,
    val labelVn: String? = null,
    val levelId: Int? = null,
    val subjectId: Int? = null,
    var isShow: Boolean = true,
    var isExpanded: Boolean = false
)