package com.zp.rx_java_t.http.api

import com.zp.rx_java_t.util.L
import com.zp.rx_java_t.util.SPUtil
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer

class HttpInterceptor : Interceptor {

    private val HTTP_TAG = "DIY"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestUrl = request.url()
        val requestBody = request.body()

        // http://192.168.0.106:6080/zxzx/app/user/login?mobile=17740894284&password=111111
//        val scheme = requestUrl.scheme()        //  http https
//        val host = requestUrl.host()            //  192.168.0.106
//        val path = requestUrl.encodedPath()     //  /zxzx/app/user/login
//        val query = requestUrl.encodedQuery()   //  mobile=17740894284&password=111111

        // 拼接的参数   可以加密
//        val params = requestUrl.queryParameterNames()
//        params.forEach {
//            L.e(HTTP_TAG,"$it  ===  ${requestUrl.queryParameter(it)}")
//        }
        // 请求体
//        val bodyStr = requestBodyToString(requestBody)
//        if (!bodyStr.isNullOrEmpty()) L.e(HTTP_TAG, bodyStr)


        val token = SPUtil.get("token", "") as String
        if (token.isNotEmpty()) { // 可以在这里更换 请求路径
            val newBuilder = request.newBuilder()
            newBuilder.addHeader("token", token)
            return chain.proceed(newBuilder.build())
        }
        return chain.proceed(request)
    }

    /**
     * RequestBody To String
     */
    private fun requestBodyToString(requestBody: RequestBody?) = Buffer().run {
        requestBody?.writeTo(this)
        readUtf8()
    }


}