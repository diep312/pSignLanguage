package com.ptit.signlanguage.network.model.response

data class User(
    var address: String? = null,
    var dateOfBirth: String? = null,
    var email: String? = null,
    var id: Int? = null,
    var language: String? = null,
    var name: String? = null,
    var password: String? = null,
    var phoneNumber: String? = null,
    var profileNumber: String? = null,
    var registerType: String? = null,
    var roles: List<String?>? = null,
    var supportedBy: String? = null,
    var token: Token? = null
)