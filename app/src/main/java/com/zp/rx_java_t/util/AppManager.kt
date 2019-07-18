package com.zp.rx_java_t.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import java.lang.ref.WeakReference
import java.util.*

/**
 * 作者    ZP
 * 日期    2019/2/15
 * 包名    com.zp.rx_java_t.util
 * 描述
 */
class AppManager {

    private var applicationCon: Context? = null

    private object Builder {
        @SuppressLint("StaticFieldLeak") val MANAGER = AppManager()
    }

    private val activities by lazy { ArrayList<WeakReference<Activity>?>() }

    companion object {
        fun getInstance() = Builder.MANAGER
    }

    fun init(applicationCon: Application) {
        this.applicationCon = applicationCon
    }

    fun getApplicationContext(): Context {
        if (applicationCon == null) throw NullPointerException("请先调用\"init()\"方法")
        return applicationCon!!
    }

    @Synchronized
    fun addActivity(activity: WeakReference<Activity>?) {
        activities.add(activity)
    }

    @Synchronized
    fun removeActivity(activity: WeakReference<Activity>?) {
        activities.remove(activity)
    }

    @Synchronized
    fun clear() {
        if (activities.isNullOrEmpty()) return
        for (activityWeakReference in activities) {
            val activity = activityWeakReference?.get()
            if (activity != null && !activity.isFinishing) {
                activity.finish()
            }
        }
        activities.clear()
    }

}