package com.zp.rx_java_t.http.api

import com.google.gson.JsonParseException
import com.google.gson.JsonSerializer
import com.zp.rx_java_t.content.Content
import com.zp.rx_java_t.content.IS_OFFICIAL
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.io.NotSerializableException
import java.lang.Exception
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException

class ApiException private constructor(var code: Int, throwable: Throwable) : Exception(throwable) {

    var msg: String? = null
    init {
        msg = throwable.message
    }

    companion object {

        const val _500_SERVICE = Content.REQUEST_SERVICE_ERROR_CODE
        /** 空指针异常 */
        const val NULL_POINTER_ERROR = 1000
        /** 连接超时异常 */
        const val TIMEOUT_ERROR = 1001
        /** 类转换异常 */
        const val CAST_ERROR = 1002
        /** 解析异常 */
        const val PARSE_ERROR = 1003
        /** 非法参数异常 */
        const val ILLEGAL_STATE_ERROR = 1004
        /** 证书异常 */
        const val SSL_ERROR = 1005
        /** 未知异常 */
        const val UNKNOW_ERROR = 1006

        fun formatException(e: Throwable): ApiException {
            val apiException: ApiException
            when (e) {
                is HttpException -> {
                    apiException = ApiException(e.code(), e)
                }
                is SocketTimeoutException -> {
                    apiException = ApiException(TIMEOUT_ERROR, e)
                    apiException.msg = "网络连接异常，请检查您的网络状态，稍后重试！"
                }
                is ConnectException -> {
                    apiException = ApiException(TIMEOUT_ERROR, e)
                    apiException.msg = "网络连接异常，请检查您的网络状态，稍后重试！"
                }
                is ConnectTimeoutException -> {
                    apiException = ApiException(TIMEOUT_ERROR, e)
                    apiException.msg = "网络连接超时，请检查您的网络状态，稍后重试！"
                }
                is UnknownHostException -> {
                    apiException = ApiException(TIMEOUT_ERROR, e)
                    apiException.msg = "网络连接异常，请检查您的网络状态，稍后重试！"
                }
                is NullPointerException -> {
                    apiException = ApiException(NULL_POINTER_ERROR, e)
                    apiException.msg = "空指针异常"
                    if (!IS_OFFICIAL) e.printStackTrace()
                }
                is javax.net.ssl.SSLHandshakeException -> {
                    apiException = ApiException(SSL_ERROR, e)
                    apiException.msg = "证书异常"
                }
                is JsonParseException -> {
                    apiException = ApiException(PARSE_ERROR, e)
                    apiException.msg = "数据解析异常"
                }
                is JSONException -> {
                    apiException = ApiException(PARSE_ERROR, e)
                    apiException.msg = "数据解析异常"
                }
                is JsonSerializer<*> -> {
                    apiException = ApiException(PARSE_ERROR, e)
                    apiException.msg = "数据解析异常"
                }
                is NotSerializableException -> {
                    apiException = ApiException(PARSE_ERROR, e)
                    apiException.msg = "数据解析异常"
                }
                is ParseException -> {
                    apiException = ApiException(PARSE_ERROR, e)
                    apiException.msg = "数据解析异常"
                }
                is ClassCastException -> {
                    apiException = ApiException(CAST_ERROR, e)
                    apiException.msg = "转换异常"
                    if (!IS_OFFICIAL) e.printStackTrace()
                }
                is IllegalStateException -> {
                    apiException = ApiException(ILLEGAL_STATE_ERROR, e)
                    apiException.msg = "参数非法"
                    if (!IS_OFFICIAL) e.printStackTrace()
                }
                else -> {
                    apiException = ApiException(UNKNOW_ERROR, e)
                    apiException.msg = "未知异常"
                    if (!IS_OFFICIAL) e.printStackTrace()
                }
            }
            return apiException
        }

    }

}