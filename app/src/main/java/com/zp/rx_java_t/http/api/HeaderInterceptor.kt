package com.zp.rx_java_t.http.api

import com.zp.rx_java_t.util.system.L
import com.zp.rx_java_t.util.system.SPUtil
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {

    private val HTTP_TAG = "http"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = SPUtil.get("token", "") as String
        if (token.isNotEmpty()) { // 可以在这里更换 请求路径
            val newBuilder = request.newBuilder()
            newBuilder.addHeader("token", token)
            L.i(HTTP_TAG, token)
            return chain.proceed(newBuilder.build())
        }
        return chain.proceed(request)
    }


}