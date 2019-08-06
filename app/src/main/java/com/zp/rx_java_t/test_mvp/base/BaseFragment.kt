package com.zp.rx_java_t.test_mvp.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zp.rx_java_t.R
import com.zp.rx_java_t.content.showToast
import com.zp.rx_java_t.test_mvp.ui.LoadDialog

abstract class BaseFragment<M : BaseModel, V : BaseView, P : BasePresenter<M, V>> : Fragment(), View.OnClickListener, BaseView {

    protected var presenter: P? = null
    private var dialog: Dialog? = null
    protected var fragmentActivity: Activity? = null
    private var rootView: View? = null

    // 当前Fragment是否处于可见状态标志，防止因ViewPager的缓存机制而导致回调函数的触发
    private var isFragmentVisible = false

    // 视图是否已经创建完成
    private var isViewCreated = false

    // 数据是否已经加载过了
    private var isLoaded = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            fragmentActivity = context
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.isFragmentVisible = isVisibleToUser
        if (!isViewCreated && !isLazy()) {
            return
        }
        if (isVisibleToUser) {
            checkViewState(null)
        } else { // 不可见
            onFragmentInvisible()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentId = getContentView()
        if (contentId <= 0) throw NullPointerException("fragment contentView is not null")
        if (rootView == null) {
            rootView = inflater.inflate(contentId, container, false)
            firstInit(savedInstanceState)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true
        checkViewState(savedInstanceState)
    }

    /** 该Fragment是否需要EventBus支持  */
    protected open fun isNeedEventBus() = false

    /**
     * 是否需要懒加载
     * @return true 为懒加载实现，在页面加载完成后需要在initAll()中调用[.setLoadedTag] 方法 <br></br>
     * false 为常规的加载
     */
    abstract fun isLazy(): Boolean

    /** 获取布局layout */
    abstract fun getContentView(): Int

    /** 获取P层 */
    abstract fun initPresenter(): P?

    abstract fun initModel(): M?

    private fun isAttachModel() = initModel() != null

    /**  第一次加载 初始化数据 */
    protected open fun firstInit(savedInstanceState: Bundle?) {
        if (isNeedEventBus()) {
//            EventBus.getDefault().register(this)
        }
        presenter = initPresenter()
        if (isAttachModel()) {
            presenter?.attachModel(initModel()!!)
        }
        presenter?.attachView(this as V)

    }

    /** 初始化 */
    abstract fun initAll(savedInstanceState: Bundle?)

    private fun checkViewState(savedInstanceState: Bundle?) {
        if (isLazy()) {
            // Fragment已经显示  视图已经创建完毕  数据还未加载完成
            if (isFragmentVisible && isViewCreated && !isLoaded) {
                initAll(savedInstanceState)
            } else {
                if (isFragmentVisible) {
                    onFragmentVisible()
                } else {
                    onFragmentInvisible()
                }
            }
        } else {
            initAll(savedInstanceState)
        }
    }

    /**
     * 页面已经加载成功的tag，表示下次再次进入不会触发 initView() initAll()方法
     * 配合 [.isLazy] 使用
     */
    protected fun setLoadedTag() {
        isLoaded = true
    }

    /**
     * Fragment已变为不可见状态
     */
    protected open fun onFragmentInvisible() = Unit

    /**
     * Fragment已变为可见状态
     */
    protected open fun onFragmentVisible() = Unit

    private fun createDialog(title: String?, isCancelable: Boolean) = LoadDialog(getViewContext(), title).apply {
        setCancelable(isCancelable)
        setCanceledOnTouchOutside(false)
    }

    override fun onClick(v: View?) = Unit

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

    override fun getViewContext() = fragmentActivity ?: activity!!

    override fun getViewFragment() = this

    override fun isRun() = isFragmentVisible

    final override fun exit() = Unit

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

    override fun onDestroyView() {
        if (isNeedEventBus()) {
//            EventBus.getDefault().unregister(this)
        }
        super.onDestroyView()
    }

    override fun onDetach() {
        fragmentActivity = null
        presenter?.detachModel()
        presenter?.detachView()
        presenter = null
        super.onDetach()
    }
}