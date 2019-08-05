package com.zp.rx_java_t.test_mvp.presenter

import com.zp.rx_java_t.content.showToast
import com.zp.rx_java_t.http.observer.CommonObserver
import com.zp.rx_java_t.http.observer.DowloadObserver
import com.zp.rx_java_t.test_mvp.base.BasePresenterImpl
import com.zp.rx_java_t.test_mvp.bean.CommonBean
import com.zp.rx_java_t.test_mvp.bean.LoginBean
import com.zp.rx_java_t.test_mvp.bean.UserInfoBean
import com.zp.rx_java_t.test_mvp.model.UserModel
import com.zp.rx_java_t.test_mvp.view.UserView
import com.zp.rx_java_t.util.system.L
import com.zp.rx_java_t.util.file.MyFileUtil
import java.io.*
import java.text.DecimalFormat

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

    private fun doubleFro2(value: Double, pattern: String = "0.00") = DecimalFormat(pattern).format(value)

    fun dowloadFile() {
        val dowloadUrl = "http://ztaqcdn.zxzx119.com/quanquan.apk"
        val fileDir = MyFileUtil.getPathForPath(MyFileUtil.OTHERS)
        val fileName = "全全_${MyFileUtil.getFileName(".apk")}"
        getMvpModel()?.downloadFile(dowloadUrl)?.subscribe(object : DowloadObserver(fileDir, fileName) {

            override fun dowloadSuccess(file: File) {
                showToast("下载成功：${file.path}")
            }

            override fun dowloadFailed() {
                showToast("下载失败")
            }

            override fun progress(byteDowload: Long, fileSize: Long, progress: Float) {
                val dowloadSize = doubleFro2(byteDowload / 1024.0 / 1024.0)
                val size = doubleFro2(fileSize / 1024.0 / 1024.0)
                L.e("dowloadSize --- $dowloadSize   size --- $size   progress --- $progress")
                getMvpView()?.dowloadProgress(dowloadSize, size, progress)
            }
        })
    }


}