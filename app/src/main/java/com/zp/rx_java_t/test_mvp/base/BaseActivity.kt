package com.zp.rx_java_t.test_mvp.base

import android.app.Activity
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.zp.rx_java_t.R
import com.zp.rx_java_t.content.getTextValue
import com.zp.rx_java_t.content.showToast
import com.zp.rx_java_t.test_mvp.ui.LoadDialog
import kotlinx.android.synthetic.main.layout_tool_bar.*
import java.lang.ref.WeakReference

abstract class BaseActivity<M : BaseModel, V : BaseView, P : BasePresenter<M, V>> : AppCompatActivity(),
        View.OnClickListener, Toolbar.OnMenuItemClickListener, BaseView {

    companion object {
        /** 竖屏  */
        protected const val PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        /** 横屏  */
        protected const val LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    /**
     * 判断当前Activity是否在前台
     */
    protected var isActive = false
    private var weakRefActivity: WeakReference<Activity>? = null

    protected var presenter: P? = null
    private var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = getDisplayState()
        val contentId = getContentView()
        if (contentId <= 0) throw NullPointerException("activity contentView is not null")
        else setContentView(contentId)
        weakRefActivity = WeakReference(this)
        AppManager.getInstance().addActivity(weakRefActivity)
        // 初始化P层
        presenter = initPresenter()
        if (isAttachModel()) {
            presenter?.attachModel(initModel()!!)
        }
        presenter?.attachView(this as V)
        // 注册EventBus
        if (isNeedEventBus()) {
//            EventBus.getDefault().register(this)
        }

        if (getBarState()) {
            initBar()
        }
        initAll(savedInstanceState)
    }

    /** 获取屏幕状态  */
    protected open fun getDisplayState() = PORTRAIT

    /** 该Activity是否需要EventBus支持  */
    protected open fun isNeedEventBus() = false

    /** 是否需要 设置 通用的ToolBar */
    protected open fun getBarState() = true

    abstract fun getContentView(): Int

    abstract fun initPresenter(): P?

    abstract fun initModel(): M?

    abstract fun initAll(savedInstanceState: Bundle?)

    private fun isAttachModel() = initModel() != null

    override fun onResume() {
        super.onResume()
        isActive = true
    }

    override fun onPause() {
        super.onPause()
        isActive = false
    }

    override fun onDestroy() {
        presenter?.detachModel()
        presenter?.detachView()
        presenter = null
        super.onDestroy()
        AppManager.getInstance().removeActivity(weakRefActivity)
    }

    override fun onMenuItemClick(item: MenuItem?) = true

    override fun onClick(v: View?) = Unit

    private fun createDialog(title: String?, isCancelable: Boolean) = LoadDialog(this, title).apply {
        setCancelable(isCancelable)
        setCanceledOnTouchOutside(false)
    }

    override fun showViewDialog(title: String?, isCancelable: Boolean?) {
        if (dialog != null) {
            dialog?.dismiss()
            dialog = null
        }
        dialog = createDialog(title, isCancelable ?: true)
        dialog?.show()
    }

    override fun dismissViewDialog() {
        if (dialog != null && dialog?.isShowing == true) {
            dialog?.dismiss()
            dialog = null
        }
    }

    override fun getViewContext() = this

    override fun getViewFragment(): Fragment? = null

    override fun isRun() = isActive

    override fun exit() {
        finish()
    }

    override fun noAnyData() {
        showToast(R.string.noAnyData)
    }

    override fun noLoadMoreData() {
        showToast(R.string.noMore)
    }

    override fun reqFailed(msg: String?, code: Int) {
        if (msg.isNullOrEmpty()) showToast(R.string.noNetWork) else showToast(msg)
    }

    override fun reqLoadMoreFailed(msg: String?, code: Int) {
        if (msg.isNullOrEmpty()) showToast(R.string.noNetWork) else showToast(msg)
    }

    private fun initBar() {
        setSupportActionBar(tool_bar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setNavigationClick()
    }

    protected fun setNavigationIcon(icon: Int) {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        tool_bar.setNavigationIcon(icon)
        setNavigationClick()
    }

    protected fun setBarTitle(title: Any) {
        supportActionBar?.title = getTextValue(title)
    }

    protected fun setNavigationClick() {
        tool_bar.setNavigationOnClickListener { exit() }
    }

    protected fun setOnMenuItemClickListener(listener: Toolbar.OnMenuItemClickListener) {
        tool_bar.setOnMenuItemClickListener(listener)
    }
}