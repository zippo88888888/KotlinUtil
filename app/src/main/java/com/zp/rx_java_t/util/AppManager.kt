package com.zp.rx_java_t.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
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

    private val activities = LinkedList<Activity>()

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
    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    @Synchronized
    fun removeActivity(activity: Activity) {
        if (activities.contains(activity)) {
            activities.remove(activity)
        }
    }

    @Synchronized
    fun clear() {
        var i = activities.size - 1
        while (i > -1) {
            val activity = activities[i]
            removeActivity(activity)
            activity.finish()
            i = activities.size
            i--
        }
    }
}