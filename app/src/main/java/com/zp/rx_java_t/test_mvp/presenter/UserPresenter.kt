package com.zp.rx_java_t.test_mvp.presenter

import com.zp.rx_java_t.content.showToast
import com.zp.rx_java_t.test_mvp.base.BasePresenterImpl
import com.zp.rx_java_t.test_mvp.bean.CommonBean
import com.zp.rx_java_t.test_mvp.bean.LoginBean
import com.zp.rx_java_t.test_mvp.bean.UserInfoBean
import com.zp.rx_java_t.test_mvp.model.UserModel
import com.zp.rx_java_t.test_mvp.view.UserView
import com.zp.rx_java_t.http.observer.CommonObserver
import com.zp.rx_java_t.util.L

class UserPresenter : BasePresenterImpl<UserModel, UserView>() {

    fun login(mobile: String?, password: String?) {
        getMvpView()?.showViewDialog()
        if (mobile.isNullOrEmpty()) {
            showToast("手机号不能为空")
            return
        }
        if (password.isNullOrEmpty()) {
            showToast("密码不能为空")
            return
        }
        getMvpModel()?.login(mobile, password)?.subscribe(object : CommonObserver<LoginBean>(getMvpView()) {
            override fun success(bean: LoginBean, state: Int) {
                getMvpView()?.loginSuccess(bean)
                getMvpView()?.dismissViewDialog()
            }
        })
    }

    fun getUserInfo() {
        getMvpView()?.showViewDialog()
        getMvpModel()?.getUserInfo()?.subscribe(object : CommonObserver<CommonBean<UserInfoBean>>(getMvpView()) {
            override fun success(bean: CommonBean<UserInfoBean>, state: Int) {
                showToast("获取用户个人信息成功")
                L.e(bean)
                getMvpView()?.dismissViewDialog()
            }
        })
    }

}