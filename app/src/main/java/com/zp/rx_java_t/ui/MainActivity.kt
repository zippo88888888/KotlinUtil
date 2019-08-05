package com.zp.rx_java_t.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.zp.rx_java_t.R
import com.zp.rx_java_t.base.BaseActivity
import com.zp.rx_java_t.base.BuilderDialog
import com.zp.rx_java_t.base.SuperSysDialog
import com.zp.rx_java_t.base.SystemDialog
import com.zp.rx_java_t.content.*
import com.zp.rx_java_t.test_mvp.ui.UserActivity
import com.zp.rx_java_t.util.system.L
import com.zp.rx_java_t.util.system.Toaster
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
            showToast(main_show.text, Toaster.T)
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
                        showToast("销毁")
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

        main_sys_http.setOnClickListener { jumpActivity(UserActivity::class.java) }
        main_sys_pwd.setOnClickListener { jumpActivity(PwdActivity::class.java) }

        main_diyView.bindView(diy_one_view_txt)
        main_animBtn.setOnClickListener {
            main_diyView.startAnim()
        }
    }

    private fun showDialog() {
        SystemDialog(this).apply {
            showDialog1({ showToast("确定") }, "申请权限", "确定")
        }
    }

    override fun onDestroy() {
        main_diyView.clear()
        super.onDestroy()
    }
}

