package com.nguyencuong.mybasekotlin.network.model.response

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("avatar")
    val image: String,
    @SerializedName("email")
    val userEmail: String,
    @SerializedName("id")
    val userId: String,
    @SerializedName("name")
    val userName: String
)