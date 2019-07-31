package com.zp.rx_java_t.test_mvp.view

import com.zp.rx_java_t.test_mvp.base.BaseView
import com.zp.rx_java_t.test_mvp.bean.LoginBean

interface UserView : BaseView {

    fun loginSuccess(loginBean: LoginBean)

}