package com.zp.rx_java_t.view

import android.content.Context
import android.os.Handler
import android.os.Message
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.view.MotionEvent
import com.zp.rx_java_t.util.system.L
import java.lang.ref.SoftReference

/**
 * 监听滚动的NestedScrollView
 */
class MyNestedScrollView : NestedScrollView {

    companion object {
        /** 静止装或停止滚动 */
        private const val SCROLL_STATE_IDLE = 0
        /** 开始触摸 */
        private const val SCROLL_STATE_TOUCH_SCROLL = 1
        /** 惯性滚动 */
        private const val SCROLL_STATE_FLING = 2

        /** 检查停止滚动的时间 */
        private const val CHECK_SCROLL_STOP_DELAY_MILLIS = 80L
        /** 标志 */
        private const val MSG_SCROLL = 1
    }

    private var handler: ScrollHandler? = null

    /** 是否触摸 */
    private var isTouched = false
    /** 滚动状态 */
    private var scrollState = SCROLL_STATE_IDLE

    /** 滚动距离监听 */
    var scrollChangeListener: ((Int) -> Unit)? = null

    /** 停止滚动 */
    var stopScrollListener:(() -> Unit) ? = null

    /** 开始滚动 */
    var startScrollListener:(() -> Unit) ? = null

    // 判断滚动顶部or底部
    private var isScrolledToTop = true
    private var isScrolledToBottom = false
    private var isUserBottom = false
    private var isUserTop = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        handler = ScrollHandler(this)
    }

    private fun restartCheckStopTiming() {
        // 先移除之前的消息
        handler?.removeMessages(MSG_SCROLL)
        handler?.sendEmptyMessageDelayed(MSG_SCROLL, CHECK_SCROLL_STOP_DELAY_MILLIS)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        handleDownEvent(ev)
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        handleUpEvent(ev)
        return super.onTouchEvent(ev)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (isTouched) {
            setScrollState(SCROLL_STATE_TOUCH_SCROLL)
        } else {
            setScrollState(SCROLL_STATE_FLING)
            restartCheckStopTiming()
        }
        scrollChangeListener?.invoke(scrollY)

        checkTopOrBottom()
    }

    private fun handleDownEvent(ev: MotionEvent) {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> isTouched = true
        }
    }

    private fun handleUpEvent(ev: MotionEvent) {
        when (ev.action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isTouched = false
                // 重新检测
                restartCheckStopTiming()
            }
        }
    }

    private fun setScrollState(state: Int) {
        if (scrollState != state) {
            scrollState = state
            when (scrollState) {
                SCROLL_STATE_IDLE -> {
                    stopScrollListener?.invoke()
                    L.e("停止滚动")
                }
                SCROLL_STATE_TOUCH_SCROLL -> {
                    startScrollListener?.invoke()
                    L.e("开始滚动")
                }
                SCROLL_STATE_FLING -> L.e("fling 惯性滚动？")
            }
        }
    }

    /**
     * 及时释放
     */
    fun clear() {
        handler?.removeMessages(MSG_SCROLL)
        handler?.removeCallbacksAndMessages(null)
        handler = null
    }

    /**
     * 检查滚动到顶部还是底部
     */
    private fun checkTopOrBottom() {
        when {
            scrollY == 0 -> {
                isScrolledToTop = true
                isScrolledToBottom = false
                isUserBottom = false
            }
            scrollY + height - paddingTop - paddingBottom == getChildAt(0).height -> {
                isScrolledToBottom = true
                isScrolledToTop = false
                isUserTop = false
            }
            else -> {
                isScrolledToTop = false
                isScrolledToBottom = false
                isUserBottom = false
                isUserTop = false
            }
        }

        if (isScrolledToTop) {
            if (!isUserTop) {
                isUserTop = true
                L.e("滚动至顶部")
            }
        } else if (isScrolledToBottom) {
            if (!isUserBottom) {
                isUserBottom = true
                L.e("滚动至底部")
            }
        }
    }

    /** 静态类部类防泄漏 */
    class ScrollHandler(infoNestedScrollView: MyNestedScrollView) : Handler() {

        private var mLastY = Integer.MIN_VALUE

        private val reference: SoftReference<MyNestedScrollView> by lazy {
            SoftReference<MyNestedScrollView>(infoNestedScrollView)
        }

        private fun get() = reference.get()

        override fun handleMessage(msg: Message?) {
            if (msg?.what == MSG_SCROLL) {
                val scrollY = get()?.scrollY
                if (get()?.isTouched == false && mLastY == scrollY) {
                    mLastY = Integer.MIN_VALUE
                    get()?.setScrollState(SCROLL_STATE_IDLE)
                } else {
                    mLastY = get()?.scrollY ?: 0
                    get()?.restartCheckStopTiming()
                }
            }
        }
    }
}