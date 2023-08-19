package com.ptit.signlanguage.network.model.response

data class User(
    var email: String? = null,
    var pass: String? = null,
    val name: String? = null,
    var token: Token? = null,
)