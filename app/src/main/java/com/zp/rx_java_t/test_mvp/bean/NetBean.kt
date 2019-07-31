package com.zp.rx_java_t.test_mvp.bean

import java.io.Serializable

open class BaseResponse : Serializable {

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