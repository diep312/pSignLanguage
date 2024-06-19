package com.ptit.signlanguage.network.api

import com.ptit.signlanguage.network.model.response.VideoToText.VideoToTextResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiPredictService {
    @Multipart
    @POST("/signlanguage/predict")
    suspend fun videoToText(@Part video: MultipartBody.Part): VideoToTextResponse?
}