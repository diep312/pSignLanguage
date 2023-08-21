package com.ptit.signlanguage.network.model.response

data class User(
    var email: String? = null,
    var id: Int? = null,
    var name: String? = null,
    var password: String? = null,
    var roles: List<String?>? = null,
    var token: Token? = null
)