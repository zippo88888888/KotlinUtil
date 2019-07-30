package com.zp.rx_java_t.http.api

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.zp.rx_java_t.util.L
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MyHttpClient {

    private var retrofit: Retrofit
    private var httpApi: HttpApi

    init {
        retrofit = createRetrofit()
        httpApi = createApi()
    }

    private object Builder {
        val builder = MyHttpClient()
    }

    companion object {
        fun getInstance() = Builder.builder
    }

    private fun createApi() = retrofit.create(HttpApi::class.java)

    fun getRequestApi() = httpApi

    private fun createRetrofit() = Retrofit.Builder().run {
        baseUrl(URL.ROOT_URL)
        client(OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(HttpInterceptor()) // 请求拦截器
                .addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                    L.i("http", it)
                }
                ).apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }).build())
        addConverterFactory(GsonConverterFactory.create())
        addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        build()
    }

}