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
    private var mToken = ""
    private const val TIME_OUT: Long = 10
    val apiService: ApiService by lazy {
        getRetrofit()!!.create(ApiService::class.java)
    }

    private fun getRetrofit(): Retrofit? {

        if(retrofit == null) {
            if (mToken == EMPTY_STRING) {
                mToken = PreferencesHelper(MyApplication.context).getString(Constants.KEY_TOKEN) ?: EMPTY_STRING
            }
//            val interceptor = HttpLoggingInterceptor()
//            interceptor.apply {
//                interceptor.level = HttpLoggingInterceptor.Level.BODY
//            }
//            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            val httpClient = OkHttpClient.Builder()
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

        return retrofit
    }
}