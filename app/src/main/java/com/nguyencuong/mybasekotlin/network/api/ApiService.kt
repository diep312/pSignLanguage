package com.nguyencuong.mybasekotlin.network.api

import com.nguyencuong.mybasekotlin.network.model.response.User
import retrofit2.http.GET

interface ApiService {
    @GET("/demo2_war_exploded/hello-servlet")
    suspend fun getUsers(): List<User>
}