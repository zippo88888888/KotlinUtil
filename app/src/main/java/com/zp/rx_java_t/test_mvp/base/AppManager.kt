package com.zp.rx_java_t.test_mvp.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import java.lang.ref.WeakReference
import java.util.*

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

    fun getApplicationContext() =
            if (applicationCon == null) throw NullPointerException("请先调用\"init()\"方法")
            else applicationCon!!

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