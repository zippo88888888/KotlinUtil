package com.zp.rx_java_t.test_mvp.base

open class BasePresenterImpl<M : BaseModel, V : BaseView> : BasePresenter<M, V> {

    private var mvpModel: M? = null
    private var mvpView: V? = null

    override fun attachModel(mvpModel: M) {
        this.mvpModel = mvpModel
    }

    override fun detachModel() {
        this.mvpModel = null
    }

    protected fun isAttachModel() = mvpModel != null

    protected fun getMvpModel() = mvpModel

    override fun attachView(mvpView: V) {
        this.mvpView = mvpView
    }

    override fun detachView() {
        this.mvpView = null
    }

    protected fun isAttachView() = mvpView != null

    protected fun getMvpView() = mvpView


}