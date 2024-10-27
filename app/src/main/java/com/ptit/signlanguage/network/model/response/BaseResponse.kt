package com.ptit.signlanguage.network.model.response

import com.google.gson.annotations.SerializedName
import com.ptit.signlanguage.ui.main.MainViewModel
import org.json.JSONObject

data class BaseResponse <T> (
    val body: T?,
    val message: String
)
data class BaseArrayResponse <T> (
    @SerializedName("body")
    val body: List<T>? = arrayListOf(),
    val message: String
)
data class BaseResponseNoBody (
    val message: String
)

data class BaseResponseV2 <T> (
    val status: String,
    val body: T,
    val message: String
)