package com.zp.rx_java_t

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.zp.rx_java_t.base.BaseActivity
import com.zp.rx_java_t.base.BuilderDialog
import com.zp.rx_java_t.base.CommonDialog
import com.zp.rx_java_t.content.getColorById
import com.zp.rx_java_t.content.toast
import com.zp.rx_java_t.content.toast2
import com.zp.rx_java_t.util.Toaster
import kotlinx.android.synthetic.main.activity_main.*

/**
 * https://blog.csdn.net/qq_38499859/article/details/81611870
 */
class MainActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_main

    override fun init(savedInstanceState: Bundle?) {
        setBarTitle("abc")
        window.statusBarColor = getColorById(R.color.baseColor)
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
    }

    private fun showDialog() {
        CommonDialog(this).apply {
            showDialog1({ toast("确定") }, "申请权限", "确定")
        }
    }

}

