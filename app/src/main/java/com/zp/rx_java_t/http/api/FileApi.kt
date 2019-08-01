package com.zp.rx_java_t.http.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface FileApi {

    @Streaming
    @GET
    fun downloadFile(@Url dowloadUrl: String): Observable<ResponseBody>

}