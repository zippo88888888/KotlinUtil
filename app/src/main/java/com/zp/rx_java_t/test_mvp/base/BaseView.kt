package com.zp.rx_java_t.test_mvp.base

import android.content.Context
import android.support.v4.app.Fragment

interface BaseView {

    /**
     * 显示 Dialog
     */
    fun showViewDialog(title: String? = "请稍后...", isCancelable: Boolean? = null)

    /**
     * 销毁 Dialog
     */
    fun dismissViewDialog()

    /**
     * 获取 Context
     */
    fun getViewContext(): Context

    /**
     * 获取 Fragment
     */
    fun getViewFragment(): Fragment?

    /**
     * 当前Activity或Fragment是否在前台运行
     */
    fun isRun(): Boolean

    /**
     * 退出
     */
    fun exit()

    /**
     * 没有任何数据
     */
    fun noAnyData()

    /**
     * 没有更多数据了
     */
    fun noLoadMoreData()

    /**
     * (刷新) 请求失败
     */
    fun reqFailed(msg: String?, code: Int)

    /**
     * 加载更多失败
     */
    fun reqLoadMoreFailed(msg: String?, code: Int)

}