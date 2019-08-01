package com.zp.rx_java_t.test_mvp.model

import android.support.v4.util.ArrayMap
import com.zp.rx_java_t.http.api.FileApi
import com.zp.rx_java_t.http.api.MyHttpClient
import com.zp.rx_java_t.http.api.Transformer
import com.zp.rx_java_t.test_mvp.base.BaseModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UserModel : BaseModel() {

    fun login(mobile: String, password: String) = MyHttpClient.getInstance().createDefaultApi()
            .login(getBody(ArrayMap<String, Any>().apply {
                put("mobile", mobile)
                put("password", password)
            }))
            .compose(Transformer.switchSchedulers())

    fun getUserInfo() =
            MyHttpClient.getInstance().createDefaultApi().getUserInfo().compose(Transformer.switchSchedulers())


    fun downloadFile(dowloadUrl: String) = MyHttpClient.getInstance()
            .createRetrofitApi("http://dowload.cn", FileApi::class.java, false)
            .downloadFile(dowloadUrl)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

}