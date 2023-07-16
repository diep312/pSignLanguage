package com.nguyencuong.mybasekotlin.network.api

import com.nguyencuong.mybasekotlin.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    private var retrofit: Retrofit? = null
    val apiService: ApiService by lazy {
        getRetrofit()!!.create(ApiService::class.java)
    }

    private fun getRetrofit(): Retrofit? {
        if(retrofit == null) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build() //Doesn't require the adapter
        }

        return retrofit
    }
}