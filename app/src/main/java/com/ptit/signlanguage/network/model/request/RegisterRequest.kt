package com.ptit.signlanguage.network.model.request

data class RegisterRequest(
    val email: String,
    val name: String,
    val password: String
)