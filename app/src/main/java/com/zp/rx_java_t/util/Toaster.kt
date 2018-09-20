package com.zp.rx_java_t.util

import android.app.Application
import android.content.Context
import android.widget.Toast
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.TextView
import android.view.ViewGroup
import android.widget.LinearLayout
import com.zp.rx_java_t.R
import com.zp.rx_java_t.content.getColorById
import com.zp.rx_java_t.content.getDisplay
import com.zp.rx_java_t.content.getStatusBarHeight
import com.zp.rx_java_t.content.getToolBarHeight

@Suppress("SENSELESS_COMPARISON")
class Toaster(con: Context) : Toast(con) {

    companion object {

        const val T = Gravity.TOP
        const val B = Gravity.BOTTOM
        const val C = Gravity.CENTER

        const val SHORT = Toast.LENGTH_SHORT
        const val LONG = Toast.LENGTH_LONG

        private var toast: Toast? = null
        private lateinit var applicationCon: Context

        fun init(applicationCon: Application) {
            this.applicationCon = applicationCon
        }

        private fun checkToast() {
            if (toast != null) toast?.cancel()
        }

        private fun getText(str: Any) = when (str) {
            is String -> str
            is Int -> applicationCon.resources.getString(str)
            else -> str.toString()
        }

        /**
         * 系统自带的 消息提醒
         */
        fun makeTextS(str: Any, duration: Int = SHORT) {
            checkToast()
            toast = makeText(applicationCon, getText(str), duration)
            toast?.show()
        }

        /**
         * 自定义 消息提醒
         * @param str           消息内容
         * @param location      位置
         * @param duration      显示时间
         * @param bgColor       背景颜色
         */
        fun makeText(str: Any, location: Int = T, duration: Int = SHORT, bgColor: Int = R.color.red) {
            checkToast()
            toast = Toast(applicationCon)
            toast?.duration = duration
            if (location != T && location != B && location != C) {
                throw IllegalArgumentException("Toaster location only is CENTER TOP or BOTTOM")
            }
            if (location == T) toast?.setGravity(location, 0, getToolBarHeight() - applicationCon.getStatusBarHeight())
            else toast?.setGravity(location, 0, 0)
            toast?.view = LayoutInflater.from(applicationCon).inflate(R.layout.layout_toast, null).apply {
                alpha = 0.8f
                translationY = -300f
                animate().translationY(0f).duration = 300
                findViewById<TextView>(R.id.toast_msg).apply {
                    text = getText(str)
                    setBackgroundColor(getColorById(bgColor))
                    layoutParams = LinearLayout.LayoutParams(applicationCon.getDisplay()[0], ViewGroup.LayoutParams.WRAP_CONTENT)
                }
            }
            toast?.show()
        }

        fun getApplicationContext(): Context {
            if (applicationCon == null) throw NullPointerException("请先调用\"init()\"方法")
            return applicationCon
        }

    }

}