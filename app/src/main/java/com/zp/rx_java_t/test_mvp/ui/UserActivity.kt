package com.zp.rx_java_t.test_mvp.ui

import android.os.Bundle
import com.zp.rx_java_t.R
import com.zp.rx_java_t.content.showToast
import com.zp.rx_java_t.test_mvp.base.BaseActivity
import com.zp.rx_java_t.test_mvp.bean.LoginBean
import com.zp.rx_java_t.test_mvp.model.UserModel
import com.zp.rx_java_t.test_mvp.presenter.UserPresenter
import com.zp.rx_java_t.test_mvp.view.UserView
import com.zp.rx_java_t.util.L
import com.zp.rx_java_t.util.SPUtil
import kotlinx.android.synthetic.main.activity_user.*

class UserActivity : BaseActivity<UserModel, UserView, UserPresenter>(), UserView {

    companion object {
        private const val SP_KEY_TOKEN = "token"
    }

    override fun getContentView() = R.layout.activity_user

    override fun initPresenter() = UserPresenter()

    override fun initModel() = UserModel()

    override fun initAll(savedInstanceState: Bundle?) {
        setBarTitle("测试登录")
        SPUtil.remove(SP_KEY_TOKEN)
        user_loginBtn.setOnClickListener {
            presenter?.login("17740894284", "1111114")
        }

        user_infoBtn.setOnClickListener {
            presenter?.getUserInfo()
        }
    }

    override fun loginSuccess(loginBean: LoginBean) {
        SPUtil.put(SP_KEY_TOKEN, loginBean.token)
        showToast("模拟登录成功：$loginBean")
        L.e("$loginBean")
    }

}
