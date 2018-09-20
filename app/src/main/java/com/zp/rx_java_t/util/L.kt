package com.zp.rx_java_t.util

import android.util.Log
import com.zp.rx_java_t.content.*

object L {

    fun d(TAG: String, message: Any) {
        if (!IS_OFFICIAL) {
            Log.d(TAG, getMessage(message))
        }
    }

    fun e(TAG: String, message: Any) {
        if (!IS_OFFICIAL) {
            Log.e(TAG, getMessage(message))
        }
    }

    fun i(TAG: String, message: Any) {
        if (!IS_OFFICIAL)
            Log.i(TAG, getMessage(message))
    }


    fun v(TAG: String, message: Any) {
        if (!IS_OFFICIAL)
            Log.i(TAG, getMessage(message))
    }

    // 下面四个是默认tag的函数
    fun i(msg: Any) {
        if (!IS_OFFICIAL)
            Log.i(LOG_TAG, getMessage(msg))
    }

    fun d(msg: Any) {
        if (!IS_OFFICIAL)
            Log.d(LOG_TAG, getMessage(msg))
    }

    fun e(msg: Any) {
        if (!IS_OFFICIAL)
            Log.e(LOG_TAG, getMessage(msg))
    }

    fun v(msg: Any) {
        if (!IS_OFFICIAL)
            Log.v(LOG_TAG, getMessage(msg))
    }

    private fun getMessage(msg: Any) = when (msg) {
        is String -> msg
        else -> msg.toString()
    }

}