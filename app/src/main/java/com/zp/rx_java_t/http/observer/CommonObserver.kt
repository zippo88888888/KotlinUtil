package com.zp.rx_java_t.http.observer

import com.zp.rx_java_t.content.Content
import com.zp.rx_java_t.content.showToast
import com.zp.rx_java_t.http.BaseResponse
import com.zp.rx_java_t.http.api.ApiException

/**
 * 通用的 Observer
 * @param T : BaseResponse  实体需继承自 BaseResponse
 * @property state Int      区分刷新还是加载更多
 */
abstract class CommonObserver<T : BaseResponse>(private var state: Int = Content.REFRESH) : BaseObserver<T>() {

    final override fun doOnNext(bean: T) {
        when (bean.code) {
            Content.REQUEST_SUCCESS_CODE -> success(bean, state)
            Content.REQUEST_SERVICE_ERROR_CODE -> failed(bean.msg, ApiException._500_SERVICE)  // 服务器内部错误
            Content.REQUEST_OTHER_LOGIN_CODE -> loginOut() // 已在其他设备登录
            Content.REQUEST_LOGIN_TIME_FAILED_CODE -> loginOut()  // 登录时间已过期
            else -> failed("未知异常", ApiException.UNKNOW_ERROR)  // 其他异常
        }
    }

    final override fun doOnError(errorMsg: String?, errorCode: Int) {
        failed(errorMsg, errorCode, state)
    }

    abstract fun success(bean: T, state: Int = Content.REFRESH)

    open fun failed(errorMsg: String?, errorCode: Int, state: Int = Content.REFRESH) {
        if (!errorMsg.isNullOrEmpty()) showToast(errorMsg)
    }

    /**
     * 退出登录
     */
    open fun loginOut() {

    }


}