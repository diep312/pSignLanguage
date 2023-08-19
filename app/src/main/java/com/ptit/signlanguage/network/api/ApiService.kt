package com.ptit.signlanguage.network.api

import com.ptit.signlanguage.network.model.request.LoginRequest
import com.ptit.signlanguage.network.model.response.BaseArrayResponse
import com.ptit.signlanguage.network.model.response.BaseResponse
import com.ptit.signlanguage.network.model.response.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("/demo2_war_exploded/hello-servlet")
    suspend fun getUsers(): List<User>

    @POST("/api/v1/login")
    suspend fun login(@Body loginRequest: LoginRequest): BaseResponse<User?>?

    @POST("/api/v1/register")
    suspend fun register(@Body user: User): BaseResponse<User>

    @GET("/api/v1/user")
    suspend fun getAllUser(): BaseArrayResponse<User>

    @GET("/api/v1/user/{id}")
    suspend fun getUserByID(@Path("id") userID: Int): BaseResponse<User>
}