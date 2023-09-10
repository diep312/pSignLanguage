package com.ptit.signlanguage.network.model.request

import com.ptit.signlanguage.network.model.response.User

data class UpdateUserRequest(
    var address: String? = null,
    var dateOfBirth: String? = null,
    var email: String? = null,
    var language: String? = null,
    var name: String? = null,
    var phoneNumber: String? = null,
    var registerType: String? = null,
    var supportedBy: String? = null
) {
    fun convertUserToUpdateUserRequest(user: User) : UpdateUserRequest {
        val updateUserRequest = UpdateUserRequest()
        updateUserRequest.address = user.address
        updateUserRequest.dateOfBirth = user.dateOfBirth
        updateUserRequest.email = user.email
        updateUserRequest.language = user.language
        updateUserRequest.name = user.name
        updateUserRequest.phoneNumber = user.phoneNumber
        updateUserRequest.registerType = user.registerType
        updateUserRequest.supportedBy = user.supportedBy
        return updateUserRequest
    }
}