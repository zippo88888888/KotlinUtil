package com.zp.rx_java_t.http

import android.os.Bundle
import com.zp.rx_java_t.R
import com.zp.rx_java_t.base.BaseActivity
import com.zp.rx_java_t.content.jumpActivity
import com.zp.rx_java_t.test_mvp.ui.UserActivity
import kotlinx.android.synthetic.main.activity_http.*

class HttpActivity : BaseActivity() {

    override fun getContentView() = R.layout.activity_http

    override fun init(savedInstanceState: Bundle?) {
        setBarTitle("http")
        http_jump.setOnClickListener {
            jumpActivity(UserActivity::class.java)
        }
    }
}


