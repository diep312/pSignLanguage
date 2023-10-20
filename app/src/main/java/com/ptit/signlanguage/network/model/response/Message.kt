package com.ptit.signlanguage.network.model.response

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("message") val message: String
)