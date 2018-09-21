package com.zp.rx_java_t.content

import android.app.Activity
import android.content.*
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.support.v4.util.ArrayMap
import android.util.SparseArray
import android.view.View
import android.view.WindowManager
import com.zp.rx_java_t.BuildConfig
import com.zp.rx_java_t.R
import com.zp.rx_java_t.util.L
import com.zp.rx_java_t.util.Toaster
import java.io.Serializable

/** 正式版---true；开发版---false */
val IS_OFFICIAL = BuildConfig.IS_OFFICIAL

const val SP_FILE_NAME = "zp_tx_java_t"
const val LOG_TAG = "APP_LOG"

// Context 相关 ===========================================================

fun Context.jumpActivity(clazz: Class<*>, map: ArrayMap<String, Any>? = null) {
    startActivity(Intent(this, clazz).apply {
        if (map != null && map.isNotEmpty()) {
            putExtras(Bundle().apply {
                map.forEach { k, v ->
                    when (v) {
                        is Int -> putInt(k, v)
                        is Double -> putDouble(k, v)
                        is Float -> putFloat(k, v)
                        is Long -> putLong(k, v)
                        is Boolean -> putBoolean(k, v)
                        is Char -> putChar(k, v)
                        is String -> putString(k, v)
                        is Serializable -> putSerializable(k, v)
                        is Parcelable -> putParcelable(k, v)
                        else -> L.e("Unsupported format")
                    }
                }
            })
        }
    })
}

/** 获取ApplicationContext */
fun getAppContext() = Toaster.getApplicationContext()

/** 返回ToolBar的高度 */
fun getToolBarHeight() = getAppContext().resources.getDimension(R.dimen.toolBarHeight).toInt()

/** 设置状态栏透明 */
fun Activity.setStatusBarTransparent() {
    val decorView = window.decorView
    val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    decorView.systemUiVisibility = option
    window.statusBarColor = Color.TRANSPARENT
}

/** 获取状态栏高度 */
fun Context.getStatusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}

/** 获取屏幕的宽，高 */
fun Context.getDisplay() = IntArray(2).apply {
    val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val point = Point()
    manager.defaultDisplay.getSize(point)
    this[0] = point.x
    this[1] = point.y
}

/** 返回当前程序版本名 */
fun Context.getAppVersionName() = packageManager.getPackageInfo(packageName, 0).versionName

/** 返回当前程序版本号 */
fun Context.getAppCode() = packageManager.getPackageInfo(packageName, 0).versionCode

/** 复制到剪贴板管理器 */
fun Context.copy(content: String) {
    val cmb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cmb.primaryClip = ClipData.newPlainText(ClipDescription.MIMETYPE_TEXT_PLAIN, content)
}

// 资源相关 ================================================

fun toast(str: Any, duration: Int = Toaster.SHORT) {
    Toaster.makeTextS(str, duration)
}

fun toast2(str: Any, location: Int = Toaster.T, duration: Int = Toaster.SHORT, bgColor: Int = R.color.red) {
    Toaster.makeText(str, location, duration, bgColor)
}

fun dip2pxF(dpValue: Float) = dpValue * getAppContext().resources.displayMetrics.density + 0.5f
fun dip2px(dpValue: Float) = dip2pxF(dpValue).toInt()
fun px2dipF(pxValue: Float) = pxValue / getAppContext().resources.displayMetrics.density + 0.5f
fun px2dip(pxValue: Float) = px2dipF(pxValue).toInt()

fun getColorById(colorID: Int) = ContextCompat.getColor(getAppContext(), colorID)
fun getDimenById(dimenID: Int) = getAppContext().resources.getDimension(dimenID)
fun getStringById(stringID: Int) = getAppContext().resources.getString(stringID)

fun getTextValue(str: Any) = when (str) {
    is Int -> getStringById(str)
    is String -> str
    else -> str.toString()
}

// 列表、集合 ===============================================

internal inline fun <E> SparseArray<E>.forEach(block: (E) -> Unit) {
    var index = 0
    val size = size()
    while (index < size) {
        block(get(index))
        index ++
    }
}

internal inline fun <E> SparseArray<E>.forEachIndices(block: (E, Int) -> Unit) {
    var index = 0
    val size = size()
    while (index < size) {
        block(get(index), index)
        index ++
    }
}

val SparseArray<*>.indices: IntRange
    get() = 0 until size()



