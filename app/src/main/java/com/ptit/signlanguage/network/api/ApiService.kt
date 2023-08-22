package com.ptit.signlanguage.network.api

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

    @GET("/api/v1/subject")
    suspend fun getListSubject(): BaseArrayResponse<Subject?>?

    @GET("/api/v1/label")
    suspend fun getListLabel(): BaseArrayResponse<Label?>?


}