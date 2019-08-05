package com.zp.rx_java_t.util.system

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.support.annotation.RequiresApi
import com.zp.rx_java_t.R
import com.zp.rx_java_t.content.IS_OFFICIAL
import com.zp.rx_java_t.content.getTextValue
import com.zp.rx_java_t.ui.PwdActivity
import java.lang.Exception

@RequiresApi(Build.VERSION_CODES.N_MR1)
object ShortcutUtil {

    private const val ID_SEARCH = "id_specialist_shortcuts_xx1"
    private const val ID_MY_ORDER = "id_specialist_shortcuts_xx2"
    private const val ID_WALLET = "id_specialist_shortcuts_xx3"

    private val shortcutIds by lazy { arrayListOf(ID_SEARCH, ID_MY_ORDER, ID_WALLET) }

    // 添加
    fun addShortcut(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.getSystemService(ShortcutManager::class.java)?.let {
                    it.dynamicShortcuts = arrayListOf(
                            buildShortcut(
                                    context,
                                    ID_SEARCH,
                                    R.string.app_name,
                                    R.drawable.ic_keyboard_del,
                                    PwdActivity::class.java
                            )
                    )
                }
                L.i("Add Shortcut Success")
            } else {
                L.e("Android 7.0以下不执行")
            }
        } catch (e: Exception) {
            if (!IS_OFFICIAL) e.printStackTrace()
            L.e("Add Shortcut Failed")
        }

    }

    private fun buildShortcut(context: Context, id: String, label: Int, icon: Int, clazz: Class<*>) =
            ShortcutInfo.Builder(context, id)
                    .setShortLabel(getTextValue(label))
                    .setLongLabel(getTextValue(label))
                    .setIcon(Icon.createWithResource(context, icon))
                    .setIntent(Intent(context, clazz).apply {
                        action = Intent.ACTION_VIEW
                    })
                    .build()

    // 清空
    fun clearShortcut(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.getSystemService(ShortcutManager::class.java)?.let {
                    it.disableShortcuts(shortcutIds)
                    it.removeDynamicShortcuts(shortcutIds)
                    L.i("Clear Shortcut Success")
                }
            }
        } catch (e: Exception) {
            if (!IS_OFFICIAL) e.printStackTrace()
            L.e("Clear Shortcut Failed")
        }
    }

}