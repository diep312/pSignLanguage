package com.ptit.signlanguage.network.model.response

import android.media.Image

data class Subject(
    val id: Int,
    val name: String,
    val name_en: String
) : java.io.Serializable