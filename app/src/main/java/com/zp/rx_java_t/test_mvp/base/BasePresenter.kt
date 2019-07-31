package com.zp.rx_java_t.test_mvp.base

interface BasePresenter<M : BaseModel, V : BaseView> {

    /**
     * presenter 与 Model 绑定
     */
    fun attachModel(mvpModel: M)

    /**
     * presenter与 Model 解绑
     */
    fun detachModel()

    /**
     * presenter 与 View 绑定
     */
    fun attachView(mvpView: V)

    /**
     * presenter与 View 解绑
     */
    fun detachView()

}