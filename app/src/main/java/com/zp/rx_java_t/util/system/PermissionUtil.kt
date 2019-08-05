package com.zp.rx_java_t.util.system

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat

object PermissionUtil {

    /** SD卡权限 对应requestCode  */
    const val WRITE_EXTERNAL_CODE = 0x101
    /** 相机权限 对应requestCode  */
    const val CAMEAR_CODE = 0x201
    /** 拨打电话权限 对应requestCode */
    const val CALL_PHONE_CODE = 0x301

    /** 读写SD卡权限  */
    const val WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
    /** 相机权限  */
    const val CAMERA = Manifest.permission.CAMERA
    /** 拨打电话权限  */
    const val CALL_PHONE = Manifest.permission.CALL_PHONE

    /**
     * 判断是否申请过权限
     * @param context   Context
     * @param permissions  权限
     * @return  返回未授权的数组
     */
    fun checkPermission(context: Context, vararg permissions: String): Array<String> {
        val list = ArrayList<String>()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                list.add(permission)
            }
        }
        return list.toTypedArray()
    }

    /**
     * 请求权限
     * @param activityOrFragment    Activity or Fragment
     * @param code                  请求码
     * @param requestPermission     权限
     */
    fun requestPermission(activityOrFragment: Any, code: Int, vararg requestPermission: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (activityOrFragment) {
                is Activity -> ActivityCompat.requestPermissions(activityOrFragment, requestPermission, code)
                is Fragment -> activityOrFragment.requestPermissions(requestPermission, code)
                else -> throw IllegalArgumentException("activityOrFragment can only be Activity or Fragment")
            }
        }
    }

    /**
     * 权限检测
     * @return  ArrayList<String> 返回权限申请失败的集合
     */
    private fun onPermissionsResult(permissions: Array<out String>, grantResults: IntArray): ArrayList<String> {
        val noPermissions = ArrayList<String>()
        for (i in grantResults.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                L.i(permissions[i] + " 权限 申请成功")
            } else {
                noPermissions.add(permissions[i])
                L.e(permissions[i] + " 权限 申请失败")
            }
        }
        return noPermissions
    }

}