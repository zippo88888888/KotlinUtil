package com.zp.rx_java_t.util.file

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.content.FileProvider
import com.zp.rx_java_t.R
import com.zp.rx_java_t.base.SystemDialog
import com.zp.rx_java_t.content.getTextValue
import java.io.File

object InstallAppUtil {

    const val REQUEST_CODE_CHECK_INSTALL = 100

    /**
     * 检测 Android 版本是否大于 O   需要检查是否有权限
     */
    fun checkAndroidVersion(activity: Activity, appFile: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android 8.0 以上
            val appName = getTextValue(R.string.app_name)
            if (activity.packageManager.canRequestPackageInstalls()) {
                install(activity, appFile)
            } else {
                SystemDialog(activity, true).apply {
                    showDialog1({
                        if (activity.packageManager.canRequestPackageInstalls()) {
                            install(activity, appFile)
                        } else {
                            activity.startActivityForResult(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }, REQUEST_CODE_CHECK_INSTALL)
                        }
                    }, "您当前系统为Android8.0或以上版本，因系统缘故，更新操作流程如下：\n" +
                            "1、点击“确定”，进入【安装未知应用】，找到【$appName】应用并允许安装\n" +
                            "2、返回${appName}应用，点击“确定”按钮，更新最新版本", "确定")
                }
            }
        } else {
            install(activity, appFile)
        }
    }

    /**
     * 安装App
     */
    private fun install(activity: Activity, appFile: File) {
        activity.startActivity(Intent().apply {
            action = Intent.ACTION_VIEW
            addCategory(Intent.CATEGORY_DEFAULT)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            setDataAndType(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(activity, "${activity.packageName}.FileProvider", appFile)
            } else {
                Uri.fromFile(appFile)
            }, "application/vnd.android.package-archive")
        })
        activity.finish()
    }

}