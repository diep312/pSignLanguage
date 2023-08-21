package com.ptit.signlanguage.network.api

import com.ptit.signlanguage.network.model.request.LoginRequest
import com.ptit.signlanguage.network.model.request.RegisterRequest
import com.ptit.signlanguage.network.model.response.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    @GET("/api/v1/user")
    suspend fun getAllUser(): BaseArrayResponse<User>

    @GET("/api/v1/user/{id}")
    suspend fun getUserByID(@Path("id") userID: Int): BaseResponse<User>

    @Multipart
    @POST("/api/v1/predict")
    suspend fun videoToText(@Part file: MultipartBody.Part): BaseResponse<VideoToTextResponse?>?
}