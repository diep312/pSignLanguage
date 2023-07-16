package com.nguyencuong.mybasekotlin.network.model.response

data class BaseResponse <T> (
    val data: T,
    val result: Boolean,
    val code:Int
)
data class BaseArrayResponse <T> (
    val body: List<T>,
    val message: String
)
data class BaseResponseNoBody (
    val message: String
)