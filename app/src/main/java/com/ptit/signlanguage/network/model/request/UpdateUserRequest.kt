package com.ptit.signlanguage.network.model.request

data class UpdateUserRequest(
    var address: String? = null,
    var dateOfBirth: String? = null,
    var email: String? = null,
    var language: String? = null,
    var name: String? = null,
    var phoneNumber: String? = null,
    var registerType: String? = null,
    var supportedBy: String? = null,
    var profileNumber : String? = null
)