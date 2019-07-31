package com.zp.rx_java_t.test_mvp.base

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody

open class BaseModel {

    fun getBody(any: Any, contentType: String = "application/json") : RequestBody {
        val json = Gson().toJson(any)
        return RequestBody.create(MediaType.parse(contentType), json)
    }

}