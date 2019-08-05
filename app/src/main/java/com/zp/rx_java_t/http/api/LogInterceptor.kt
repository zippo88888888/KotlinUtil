package com.zp.rx_java_t.http.api

import com.zp.rx_java_t.util.system.L
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import java.nio.charset.Charset

class LogInterceptor : Interceptor {

    private val HTTP_TAG = "http"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestUrl = request.url()
        val requestBody = request.body()

        // http://192.168.0.106:6080/zxzx/app/user/login?mobile=17740894284&password=111111
//        val scheme = requestUrl.scheme()        //  http https
//        val host = requestUrl.host()            //  192.168.0.106
//        val path = requestUrl.encodedPath()     //  /zxzx/app/user/login
//        val query = requestUrl.encodedQuery()   //  mobile=17740894284&password=111111

        // 拼接的参数
//        val params = requestUrl.queryParameterNames()
//        params.forEach {
//            L.e(HTTP_TAG,"$it  ===  ${requestUrl.queryParameter(it)}")
//        }

        val bodyStr = requestBodyToString(requestBody)
        L.i(HTTP_TAG, " ---> ${request.method() }  ${requestUrl.url()}")
//        L.i(HTTP_TAG, "Content-Type:  ${requestBody?.contentType()}")
//        L.i(HTTP_TAG, "Content-Length:  ${requestBody?.contentLength()}")
        if (!bodyStr.isNullOrEmpty()) L.i(HTTP_TAG, bodyStr)

        val response = chain.proceed(request)
        val headers = response.headers()
        for (i in 0 until headers.size()) {
            L.i(HTTP_TAG, "${headers.name(i)}：${headers.value(i)}")
        }
        val responseBody = response.body()
        val source = responseBody?.source()
//        source?.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
        val buffer = source?.buffer()
        L.i(HTTP_TAG, "<-- END HTTP (binary ${buffer?.size() ?: 0} -byte body omitted)")
        var charset = Charset.forName("UTF-8")
        val contentType = responseBody?.contentType()
        if (contentType != null) {
            charset = contentType.charset(Charset.forName("UTF-8"))
        }
        L.i(HTTP_TAG,"json ---> ${buffer?.readString(charset)}")

        return response

    }

    /**
     * RequestBody To String
     */
    private fun requestBodyToString(requestBody: RequestBody?) = Buffer().run {
        requestBody?.writeTo(this)
        readUtf8()
    }
}