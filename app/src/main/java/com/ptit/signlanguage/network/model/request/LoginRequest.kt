package com.ptit.signlanguage.network.model.request

data class LoginRequest(
    val password: String,
    val username: String
)