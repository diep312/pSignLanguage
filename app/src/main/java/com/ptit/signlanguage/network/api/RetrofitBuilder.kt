package com.ptit.signlanguage.network.api

import com.ptit.signlanguage.BuildConfig
import com.ptit.signlanguage.base.MyApplication
import com.ptit.signlanguage.data.prefs.PreferencesHelper
import com.ptit.signlanguage.utils.Constants
import com.ptit.signlanguage.utils.Constants.EMPTY_STRING
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {

    private var retrofit: Retrofit? = null
    private var retrofitLogin: Retrofit? = null
    private var mToken = ""
    private const val TIME_OUT: Long = 10
    private var apiService: ApiService? = null
    val apiServiceLogin: ApiServiceLogin by lazy {
        getRetrofitLogin()!!.create(ApiServiceLogin::class.java)
    }

    fun getApiService(): ApiService? {
        if (apiService == null) {
            apiService = getRetrofit()!!.create(ApiService::class.java)
        }
        return apiService
    }

    fun resetApiService() {
        initRetrofit()
        apiService = getRetrofit()!!.create(ApiService::class.java)
    }

    private fun getRetrofit(): Retrofit? {
        if (retrofit == null) {
            initRetrofit()
        }
        return retrofit
    }

    private fun getRetrofitLogin(): Retrofit? {

        if (retrofitLogin == null) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.apply {
                interceptor.level = HttpLoggingInterceptor.Level.BODY
            }

            val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(interceptor)
            httpClient.connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            httpClient.readTimeout(TIME_OUT, TimeUnit.SECONDS)

            httpClient.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .build()
                chain.proceed(request)
            }

            retrofitLogin = Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build() //Doesn't require the adapter
        }

        return retrofitLogin
    }

    private fun initRetrofit() {
        mToken = PreferencesHelper(MyApplication.context).getString(Constants.KEY_TOKEN)
            ?: EMPTY_STRING
        val interceptor = HttpLoggingInterceptor()
        interceptor.apply {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        }
//            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor)
        httpClient.connectTimeout(TIME_OUT, TimeUnit.SECONDS)
        httpClient.readTimeout(TIME_OUT, TimeUnit.SECONDS)

        httpClient.addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .addHeader("Authorization", "Bearer $mToken")
                .build()
            chain.proceed(request)
        }

        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_URL)
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build() //Doesn't require the adapter
    }
}