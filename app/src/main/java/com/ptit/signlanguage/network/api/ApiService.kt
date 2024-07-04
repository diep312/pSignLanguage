package com.ptit.signlanguage.network.api

import com.ptit.signlanguage.network.model.request.UpdateUserRequest
import com.ptit.signlanguage.network.model.response.*
import com.ptit.signlanguage.network.model.response.VideoToText.VideoToTextResponse
import com.ptit.signlanguage.network.model.response.check_video.CheckVideoRes
import com.ptit.signlanguage.network.model.response.score_with_subject.ScoreWithSubject
import com.ptit.signlanguage.network.model.response.subjectWrap.SubjectWrap
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @GET("/api/v1/user")
    suspend fun getAllUser(): BaseArrayResponse<User>

    @GET("/api/v1/user/{id}")
    suspend fun getUserByID(
        @Path("id") userID: Int,
    ): BaseResponse<User>

    @Multipart
    @POST("/api/v1/predict")
    suspend fun videoToText(
        @Part file: MultipartBody.Part,
    ): BaseResponse<VideoToTextResponse?>?

    @Multipart
    @POST("/api/v1/checkVideo")
    suspend fun checkVideo(
        @Part("label") label: RequestBody,
        @Part file: MultipartBody.Part,
    ): BaseResponse<CheckVideoRes?>?

    @GET("/api/v1/subject")
    suspend fun getListSubject(): BaseArrayResponse<Subject?>?

    @GET("/api/v1/label")
    suspend fun getListLabel(): BaseArrayResponse<Label?>?

    @GET("/api/v1/video")
    suspend fun getVideo(
        @Query("label") label: String,
    ): BaseResponse<Video?>?

    @GET("/api/v1/list-labels-by-subjectId")
    suspend fun getAllInfoSubject(
        @Query("subjectId") subjectId: Int,
    ): BaseResponse<SubjectWrap?>?

    @PUT("/api/v1/user")
    suspend fun updateUser(
        @Body updateUserRequest: UpdateUserRequest,
    ): BaseResponse<User?>

    @FormUrlEncoded
    @POST("/api/v1/scoreWithSubject")
    suspend fun getScoreWithSubject(
        @Field("levelIds") levelIds: Int,
        @Field("subjectIds") subjectIds: Int,
    ): BaseResponse<ScoreWithSubject?>?
}
