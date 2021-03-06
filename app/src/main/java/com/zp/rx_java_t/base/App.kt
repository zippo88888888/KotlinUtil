package com.zp.rx_java_t.base

import android.app.Application
import com.zp.rx_java_t.test_mvp.base.AppManager

/**
 * com.zp.rx_java_t.base
 * Created by ZP on 2018/9/20.
 * description:
 * version: 1.0
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AppManager.getInstance().init(this)
    }

}