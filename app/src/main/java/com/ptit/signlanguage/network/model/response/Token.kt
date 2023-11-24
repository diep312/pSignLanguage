package com.ptit.signlanguage.network.model.response

data class Token(
    val accessToken: String,
    val refreshToken: String
)