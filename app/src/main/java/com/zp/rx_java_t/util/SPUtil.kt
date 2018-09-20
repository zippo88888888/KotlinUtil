package com.zp.rx_java_t.util

import android.content.Context
import android.content.SharedPreferences
import com.zp.rx_java_t.content.*
import com.zp.rx_java_t.content.getAppContext
import java.lang.reflect.Method


object SPUtil {

    /**
     * 保存数据的方法，先拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     */
    fun put(key: String, any: Any) {
        val editor = getSP().edit()
        when (any) {
            is String -> editor.putString(key, any)
            is Int -> editor.putInt(key, any)
            is Boolean -> editor.putBoolean(key, any)
            is Float -> editor.putFloat(key, any)
            is Long -> editor.putLong(key, any)
            else -> editor.putString(key, any.toString())
        }
        SharedPreferencesCompat.apply(editor)
    }

    /**
     * 得到保存数据的方法，根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     * @param key           键
     * @param defaultObject 默认类型，默认值
     */
    fun get(key: String, defaultObject: Any): Any? = when (defaultObject) {
            is String -> getSP().getString(key, defaultObject)
            is Int -> getSP().getInt(key, defaultObject)
            is Boolean -> getSP().getBoolean(key, defaultObject)
            is Float -> getSP().getFloat(key, defaultObject)
            is Long -> getSP().getLong(key, defaultObject)
            else -> null
        }

    /**
     * 移除某个key值以及对应的值
     */
    fun remove(key: String) {
        val editor = getSP().edit()
        editor.remove(key)
        SharedPreferencesCompat.apply(editor)
    }

    /**
     * 清除所有数据
     */
    fun clear() {
        val editor = getSP().edit()
        editor.clear()
        SharedPreferencesCompat.apply(editor)
    }

    /**
     * 查询某个key是否已经存在
     */
    fun contains(key: String): Boolean = getSP().contains(key)

    /**
     * 返回所有的键值对
     */
    fun getAll(): Map<String, *> =  getSP().all

    private fun getSP(): SharedPreferences =
            getAppContext().getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE)

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类(内部类)
     */
    private object SharedPreferencesCompat {
        private val sApplyMethod = findApplyMethod()

        /**
         * 反射查找apply的方法
         */
        private fun findApplyMethod(): Method? {
            try {
                val clz = SharedPreferences.Editor::class.java
                return clz.getMethod("apply")
            } catch (e: NoSuchMethodException) {
                if (!IS_OFFICIAL) e.printStackTrace()
            }
            return null
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         */
        fun apply(editor: SharedPreferences.Editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor)
                    return
                }
            } catch (e: Exception) {
            }
            editor.commit()
        }
    }

}