package com.zp.rx_java_t.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.zp.rx_java_t.content.getAppContext
import com.zp.rx_java_t.content.getStatusBarHeight

abstract class BaseFragment : Fragment(), View.OnClickListener {

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

    /** 获取状态栏的 LayoutParams  */
    protected open fun getStatusBarLP() = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, fragmentActivity?.getStatusBarHeight()
            ?: getAppContext().getStatusBarHeight())


    /**
     * 是否需要懒加载
     * @return true 为懒加载实现，在页面加载完成后需要在initAll()中调用[.setLoadedTag] 方法 <br></br>
     * false 为常规的加载
     */
    abstract fun isLazy(): Boolean

    /** 获取布局layout */
    abstract fun getContentView(): Int

    /**  第一次加载 初始化数据 */
    protected open fun firstInit(savedInstanceState: Bundle?) {
        // 比如实例P层
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

    open fun exit() {
        fragmentActivity?.finish()
    }

    override fun onClick(v: View) = Unit

    override fun onDetach() {
        fragmentActivity = null
        super.onDetach()
    }


}