package com.zp.rx_java_t.http.api

import com.zp.rx_java_t.http.CommonBean
import com.zp.rx_java_t.http.LoginBean
import com.zp.rx_java_t.http.UserInfoBean
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HttpApi {

    @POST(URL.LOGIN)
    fun login(@Body body: RequestBody): Observable<LoginBean>

    @GET(URL.GET_USER_INFO)
    fun getUserInfo(): Observable<CommonBean<UserInfoBean>>

}