package com.zp.rx_java_t.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.EditText

/**
 * 可以与NestedScrollView 共用的EditText
 */
class NestedEditText : EditText {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val oldy = event.y.toInt()
        val action = event.actionMasked
        if (action == MotionEvent.ACTION_DOWN) {
            parent.requestDisallowInterceptTouchEvent(true)
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (canVerticalScroll(this)) {
                parent.requestDisallowInterceptTouchEvent(true)
            } else {
                parent.requestDisallowInterceptTouchEvent(false)
            }
        } else if (action == MotionEvent.ACTION_UP) {
            parent.requestDisallowInterceptTouchEvent(false)
        }
        return super.onTouchEvent(event)
    }

    /**
     * EditText竖直方向是否可以滚动
     * @param editText  需要判断的EditText
     * @return  true：可以滚动   false：不可以滚动
     */
    private fun canVerticalScroll(editText: EditText): Boolean {
        //滚动的距离
        val scrollY = editText.scrollY
        //控件内容的总高度
        val scrollRange = editText.layout.height
        //控件实际显示的高度
        val scrollExtent = editText.height - editText.compoundPaddingTop - editText.compoundPaddingBottom
        //控件内容总高度与实际显示高度的差值
        val scrollDifference = scrollRange - scrollExtent
        return if (scrollDifference == 0) false
        else scrollY > 0 || scrollY < scrollDifference - 1
    }

}