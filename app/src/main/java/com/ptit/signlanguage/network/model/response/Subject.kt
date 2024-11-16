package com.ptit.signlanguage.network.model.response

import android.media.Image
import com.google.gson.annotations.SerializedName

data class Subject(
    val id: Int,
    val name: String = "",
    val name_en: String? = "",
    val totalLabels: Int = 0,
    val learnedLabels: Int = 0
) : java.io.Serializable