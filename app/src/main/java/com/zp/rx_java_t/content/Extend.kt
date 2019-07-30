package com.zp.rx_java_t.content

import android.app.Activity
import android.content.*
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.util.ArrayMap
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.zp.rx_java_t.BuildConfig
import com.zp.rx_java_t.R
import com.zp.rx_java_t.util.AppManager
import com.zp.rx_java_t.util.L
import com.zp.rx_java_t.util.Toaster
import java.io.File
import java.io.Serializable
import java.lang.Exception
import java.text.DecimalFormat
import java.util.*

/** 正式版---true；开发版---false */
val IS_OFFICIAL = BuildConfig.IS_OFFICIAL

const val SP_FILE_NAME = "zp_rx_java_t"
const val LOG_TAG = "APP_LOG"

// Context 相关 ===========================================================
fun Context.jumpActivity(clazz: Class<*>, map: ArrayMap<String, Any>? = null) {
    startActivity(Intent(this, clazz).apply {
        if (!map.isNullOrEmpty()) {
            putExtras(getBundleFormMapKV(map))
        }
    })
}

fun Fragment.jumpActivity(clazz: Class<*>, map: ArrayMap<String, Any>? = null) {
    activity?.jumpActivity(clazz, map)
}

fun Activity.jumpActivity(clazz: Class<*>, requestCode: Int, map: ArrayMap<String, Any>? = null) {
    startActivityForResult(Intent(this, clazz).apply {
        if (!map.isNullOrEmpty()) {
            putExtras(getBundleFormMapKV(map))
        }
    }, requestCode)
}

/**
 * 根据Map 获取 Bundle 扩展
 */
fun getBundleFormMapKV(map: ArrayMap<String, Any>) = Bundle().apply {
    for ((k, v) in map) {
        when (v) {
            is Int -> putInt(k, v)
            is Double -> putDouble(k, v)
            is Float -> putFloat(k, v)
            is Long -> putLong(k, v)
            is Boolean -> putBoolean(k, v)
            is Char -> putChar(k, v)
            is String -> putString(k, v)
            is Serializable -> putSerializable(k, v)
            is Bundle -> putAll(v)
            is Parcelable -> putParcelableArrayList(k, v as ArrayList<out Parcelable>?)
            else -> L.e("Unsupported format")
        }
    }
}

fun <T : Parcelable> getBundleForParcelable(key: String, t: T) = Bundle().apply {
    putParcelable(key, t)
}

/** 获取全局的ApplicationContext */
fun getAppContext() = AppManager.getInstance().getApplicationContext()

/** 返回ToolBar的高度 */
fun getToolBarHeight() = getAppContext().resources.getDimension(R.dimen.toolBarHeight).toInt()

/**
 * 设置状态栏 显示状态
 * @param enable true: 隐藏；false：显示
 */
fun Activity.setFullScreen(enable: Boolean) {
    val lp = window.attributes
    if (enable) lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
    else lp.flags = lp.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
    window.attributes = lp
    window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
}

/** 设置状态栏透明 */
fun Activity.setStatusBarTransparent() {
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
        val decorView = window.decorView
        val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        decorView.systemUiVisibility = option
        window.statusBarColor = Color.TRANSPARENT
    }
}

/** 获取状态栏高度 */
fun Context.getStatusBarHeight() = resources.getDimensionPixelSize(
        resources.getIdentifier("status_bar_height", "dimen", "android")
)

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

/** 关闭软键盘 */
fun Activity.closeKeyboard() {
    try {
        val m = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (m.isActive) { // 表示打开
            // 如果打开，则关闭
            m.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    } catch (e: Exception) {
        if (!IS_OFFICIAL) e.printStackTrace()
    }
}

/**
 * 根据包名检测某个APP是否安装
 * @param packageName 包名
 * @return true-安装；false-没有安装
 */
fun isInstallByRead(packageName: String) = File("/data/data/$packageName").exists()

/**
 * 打开第三方App
 * @param pkg 第三方应用的包名
 * @param cls 第三方应用的进入的第一个Activity
 */
fun Context.jumpToOtherApp(pkg: String, cls: String) {
    try {
        startActivity(Intent().apply {
            component = ComponentName(pkg, cls)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    } catch (e: Exception) {
        if (!IS_OFFICIAL) e.printStackTrace()
        showToast("打开失败：该应用未安装或该应用不是最新版本")
    }
}

/** 根据Tag检查是否存在Fragment实例，如果存在就移除！ */
fun AppCompatActivity.checkFragmentByTag(fragmentTag: String) {
    val fragment = supportFragmentManager.findFragmentByTag(fragmentTag)
    if (fragment != null) {
        supportFragmentManager.beginTransaction().remove(fragment).commit()
    }
}

/**
 * 根据ViewPager 和 FragmentPagerAdapter 获取 当前选中的Fragment
 * @param vpAdapter         FragmentPagerAdapter
 * @param viewPager         ViewPager
 */
fun <T : Fragment> AppCompatActivity.getFragment(vpAdapter: FragmentPagerAdapter, viewPager: ViewPager): T? {
    val fragmentId = vpAdapter.getItemId(viewPager.currentItem)
    val tag = "android:switcher:${viewPager.id}:$fragmentId"
    return supportFragmentManager.findFragmentByTag(tag) as T
}

// UI  相关 ================================================

fun showToast(any: Any, duration: Int = Toaster.SHORT) {
    Toaster.makeTextS(any, duration)
}

/**
 * 计算View的大小
 * @param defaultSize Int   默认值
 * @param measureSpec Int   测量模式
 */
fun getSize(defaultSize: Int, measureSpec: Int): Int {
    var viewSize = defaultSize
    // 获取测量模式
    val mode = View.MeasureSpec.getMode(measureSpec)
    when (mode) {
        View.MeasureSpec.UNSPECIFIED -> { // 未指定大小
            viewSize = defaultSize
        }
        View.MeasureSpec.AT_MOST -> { // 理解为 wrap_content
            viewSize = defaultSize
        }
        View.MeasureSpec.EXACTLY -> { // match_parent或具体的值
            // 根据测量模式获取其尺寸
            val size = View.MeasureSpec.getSize(measureSpec)
            viewSize = size
        }
    }
    return viewSize
}

// 资源相关 ================================================

fun dip2pxF(dpValue: Float) = dpValue * getAppContext().resources.displayMetrics.density + 0.5f
fun dip2px(dpValue: Float) = dip2pxF(dpValue).toInt()
fun px2dipF(pxValue: Float) = pxValue / getAppContext().resources.displayMetrics.density + 0.5f
fun px2dip(pxValue: Float) = px2dipF(pxValue).toInt()

fun getColorById(colorID: Int) = ContextCompat.getColor(getAppContext(), colorID)
fun getDimenById(dimenID: Int) = getAppContext().resources.getDimension(dimenID)
fun getStringById(stringID: Int) = getAppContext().resources.getString(stringID)

fun getTextValue(any: Any) = try {
    when (any) {
        is Int -> getStringById(any)
        is String -> any
        else -> any.toString()
    }
} catch (e: Exception) {
    any.toString()
}

/**
 * double 保留N位小数
 */
fun doubleFro2(value: Double, pattern: String = "0.00") = DecimalFormat(pattern).format(value)

// 列表、集合 ===============================================

internal inline fun SparseBooleanArray.forEach(block: (Int) -> Unit) {
    var index = 0
    val size = size()
    while (index < size) {
        block(index)
        index++
    }
}

internal inline fun <E> SparseArray<E>.forEach(block: (E) -> Unit) {
    var index = 0
    val size = size()
    while (index < size) {
        block(get(index))
        index++
    }
}

internal inline fun <E> SparseArray<E>.forEachIndices(block: (E, Int) -> Unit) {
    var index = 0
    val size = size()
    while (index < size) {
        block(get(index), index)
        index++
    }
}

val SparseArray<*>.indices: IntRange
    get() = 0 until size()

internal inline fun <E> List<E>.forEachNoIterable(block: (E) -> Unit) {
    var index = 0
    val size = size
    while (index < size) {
        block(get(index))
        index++
    }
}

internal fun <E> LinkedList<E>.forEach(block: (E, Int) -> Unit) {
    var i = size - 1
    while (i > -1) {
        block(get(i), i)
        i = size
        i--
    }
}

