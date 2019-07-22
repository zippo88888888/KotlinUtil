package com.zp.rx_java_t.ui

import android.os.Bundle
import com.zp.rx_java_t.R
import com.zp.rx_java_t.base.BaseActivity

class PwdActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_pwd

    override fun init(savedInstanceState: Bundle?) {
        setBarTitle("PWD")
    }

}
