package com.ptit.signlanguage.network.api

import com.ptit.signlanguage.network.model.response.User
import retrofit2.http.GET

interface ApiService {
    @GET("/demo2_war_exploded/hello-servlet")
    suspend fun getUsers(): List<User>
}