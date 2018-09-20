package com.zp.rx_java_t

import android.os.Bundle
import com.zp.rx_java_t.base.BaseActivity
import com.zp.rx_java_t.content.getColorById
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
    }

}

