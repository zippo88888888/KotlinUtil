package com.zp.rx_java_t.http.api

import com.zp.rx_java_t.test_mvp.bean.CommonBean
import com.zp.rx_java_t.test_mvp.bean.LoginBean
import com.zp.rx_java_t.test_mvp.bean.UserInfoBean
import io.reactivex.Observable
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface HttpApi {

    @POST(URL.LOGIN)
    fun login(@Body body: RequestBody): Observable<LoginBean>

    @GET(URL.GET_USER_INFO)
    fun getUserInfo(): Observable<CommonBean<UserInfoBean>>

}