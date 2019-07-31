package com.zp.rx_java_t.test_mvp.model

import android.support.v4.util.ArrayMap
import com.zp.rx_java_t.http.api.MyHttpClient
import com.zp.rx_java_t.http.api.Transformer
import com.zp.rx_java_t.test_mvp.base.BaseModel

class UserModel : BaseModel() {

    fun login(mobile: String, password: String) = MyHttpClient.getInstance().getRequestApi()
            .login(getBody(ArrayMap<String, Any>().apply {
                put("mobile", mobile)
                put("password", password)
            }))
            .compose(Transformer.switchSchedulers())

    fun getUserInfo() =
            MyHttpClient.getInstance().getRequestApi().getUserInfo().compose(Transformer.switchSchedulers())


}