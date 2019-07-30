package com.zp.rx_java_t.http

import android.os.Bundle
import android.support.v4.util.ArrayMap
import com.google.gson.Gson
import com.zp.rx_java_t.R
import com.zp.rx_java_t.base.BaseActivity
import com.zp.rx_java_t.content.showToast
import com.zp.rx_java_t.http.api.MyHttpClient
import com.zp.rx_java_t.http.api.Transformer
import com.zp.rx_java_t.http.observer.CommonObserver
import com.zp.rx_java_t.util.L
import com.zp.rx_java_t.util.SPUtil
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_http.*
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.Serializable

class HttpActivity : BaseActivity() {

    private lateinit var userModel: UserModel

    override fun getContentView() = R.layout.activity_http

    override fun init(savedInstanceState: Bundle?) {
        setBarTitle("http")
        userModel = UserModelImpl()
        http_login.setOnClickListener {
            login()
        }
        http_getUserInfo.setOnClickListener {
            getUserInfo()
        }
    }

    private fun login() {
        userModel.login().subscribe(object : CommonObserver<LoginBean>(){
            override fun success(bean: LoginBean, state: Int) {
                showToast(bean)
                SPUtil.put("token", bean.token)
            }
        })
    }

    private fun getUserInfo() {
        userModel.getUserInfo().subscribe(object : CommonObserver<CommonBean<UserInfoBean>>() {
            override fun success(bean: CommonBean<UserInfoBean>, state: Int) {
                L.i("获取用户基本信息成功：$bean")
            }
        })
    }

}

interface BaseModel {

    fun getBody(any: Any, contentType: String = "application/json") : RequestBody {
        val json = Gson().toJson(any)
        return RequestBody.create(MediaType.parse(contentType), json)
    }


}


interface UserModel : BaseModel {
    fun login(): Observable<LoginBean>

    fun getUserInfo(): Observable<CommonBean<UserInfoBean>>
}

class UserModelImpl : UserModel {

    override fun login() = MyHttpClient.getInstance().getRequestApi()
            .login(getBody(ArrayMap<String, Any>().apply {
                put("mobile", "17740894284")
                put("password", "111111")
            }))
            .compose(Transformer.switchSchedulers())

    override fun getUserInfo() =
            MyHttpClient.getInstance().getRequestApi().getUserInfo().compose(Transformer.switchSchedulers())


}

open class BaseResponse {
    open var msg: String = ""
    open var code: Int = -1
}

data class CommonBean<T>(
        var data: T?
) : BaseResponse(), Serializable

data class LoginBean(
        var expire: Long = 0L,
        var token: String = ""
) : BaseResponse(), Serializable

data class UserInfoBean(
        var id: Long = 0L,
        var name: String = "",
        var phone: String = "",
        var head: String = "",
        var money: Long = 0L,
        var alipayAccount: String = "",
        var infomation: String = "",
        var auth: String = "",
        var realName: String = "",
        var levelTitle: String = ""
) : Serializable
