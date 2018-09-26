package com.zp.rx_java_t

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.ImageView
import android.widget.TextView
import com.zp.rx_java_t.base.BaseActivity
import com.zp.rx_java_t.base.BuilderDialog
import com.zp.rx_java_t.base.SuperSysDialog
import com.zp.rx_java_t.base.SystemDialog
import com.zp.rx_java_t.content.getColorById
import com.zp.rx_java_t.content.jumpActivity
import com.zp.rx_java_t.content.toast
import com.zp.rx_java_t.content.toast2
import com.zp.rx_java_t.ui.ListActivity
import com.zp.rx_java_t.util.L
import com.zp.rx_java_t.util.Toaster
import kotlinx.android.synthetic.main.activity_main.*

/**
 * https://blog.csdn.net/qq_38499859/article/details/81611870
 */
class MainActivity : BaseActivity() {

    private val items by lazy {
        arrayOf("item1", "item2", "item3")
    }

    override fun getContentView() = R.layout.activity_main

    override fun init(savedInstanceState: Bundle?) {
        setBarTitle("Kotlin 相关")
        window.statusBarColor = getColorById(R.color.baseColor)
        main_list.setOnClickListener { jumpActivity(ListActivity::class.java) }
        main_show.setOnClickListener {
            toast2(main_show.text, Toaster.T)
        }
        main_dialog.setOnClickListener {
            BuilderDialog.create()
                    .setDialogFM(supportFragmentManager)
                    .setContentView(R.layout.dialog_test)
                    .setInitLogic { v, dialog ->
                        v.findViewById<ImageView>(R.id.dialog_permission_close).setOnClickListener { dialog.dismiss() }
                        v.findViewById<TextView>(R.id.dialog_permission_down).setOnClickListener { showDialog() }
                    }
                    .setDismissLogic {
                        toast2("dialog 销毁", Toaster.C, Toaster.LONG, R.color.black)
                    }
                    .show()
        }

        main_sys_dialog.setOnClickListener {
            SuperSysDialog.create(this)
                    .setTitle("我是标题")
                    /*.setMessage("I am Message")*/
                    /*.setSingleItems(items) { _, i ->
                        L.e("选中了$i")
                    }
                    .setSingleSelectIndex(0)*/
                    .setMultiChoiceItems(items) { _, i, f ->
                        L.e(if (f) "选中了$i" else "取消选中$i")
                    }
                    .setMultcheckedItems(BooleanArray(items.size) { it % 2 == 0 })
                    .setCancelable(false)
                    .setPositiveButton("确定") { d, _ -> d.dismiss() }
                    .setNegativeButton("取消") { d, _ -> d.dismiss() }
                    .setNeutralButton("不再显示") { d, _ -> d.dismiss() }
                    .builder()
        }
    }

    private fun showDialog() {
        SystemDialog(this).apply {
            showDialog1({ toast("确定") }, "申请权限", "确定")
        }
    }

}

