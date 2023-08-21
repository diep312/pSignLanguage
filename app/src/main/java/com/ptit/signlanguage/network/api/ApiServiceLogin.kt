package com.ptit.signlanguage.network.api

import com.ptit.signlanguage.network.model.request.LoginRequest
import com.ptit.signlanguage.network.model.request.RegisterRequest
import com.ptit.signlanguage.network.model.response.BaseResponse
import com.ptit.signlanguage.network.model.response.Token
import com.ptit.signlanguage.network.model.response.User
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServiceLogin {
    @POST("/api/v1/login")
    suspend fun login(@Body loginRequest: LoginRequest): BaseResponse<User?>?

    @POST("/api/v1/register")
    suspend fun register(@Body registerRequest: RegisterRequest): BaseResponse<Token?>?
}