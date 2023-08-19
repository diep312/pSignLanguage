package com.ptit.signlanguage.network.model.response

data class BaseResponse <T> (
    val body: T?,
    val message: String
)
data class BaseArrayResponse <T> (
    val body: List<T>? = arrayListOf(),
    val message: String
)
data class BaseResponseNoBody (
    val message: String
)