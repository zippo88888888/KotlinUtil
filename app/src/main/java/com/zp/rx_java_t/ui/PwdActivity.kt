package com.zp.rx_java_t.ui

import android.os.Bundle
import com.zp.rx_java_t.R
import com.zp.rx_java_t.base.BaseActivity
import com.zp.rx_java_t.content.showToast
import com.zp.rx_java_t.util.L
import com.zp.rx_java_t.view.diy.DiyInputView
import kotlinx.android.synthetic.main.activity_pwd.*

class PwdActivity : BaseActivity(), DiyInputView.InputValueListener {

    override fun getContentView() = R.layout.activity_pwd

    override fun init(savedInstanceState: Bundle?) {
        setBarTitle("PWD")

        pwd_diyInputView.bindBoardView(pwd_keyBoardView)
        pwd_diyInputView.inputValueListener = this

        pwd_iFrameTxtHiddenCB.setOnClickListener {
            for (i in 0 until pwd_rg.childCount) {
                pwd_rg.getChildAt(i).isEnabled = pwd_iFrameTxtHiddenCB.isChecked
            }
            pwd_diyInputView.frameTxtHidden = pwd_iFrameTxtHiddenCB.isChecked
        }
        pwd_rg.setOnCheckedChangeListener { _, checkedId ->
            pwd_diyInputView.frameTxtHiddenType = when (checkedId) {
                R.id.pwd_circleRB -> DiyInputView.FRAME_TXT_HIDDEN_TYPE_CIRCLE
                R.id.pwd_rectangleRB -> DiyInputView.FRAME_TXT_HIDDEN_TYPE_RECTANGLE
                else -> DiyInputView.FRAME_TXT_HIDDEN_TYPE_TRIANGLE
            }
        }
    }

    override fun input(inputValue: String, value: String) {
        L.e("正在输入：$inputValue  ===  已经输入：$value")
    }

    override fun inputEnd(value: String) {
        showToast("输入完成：$value")
    }

    override fun onDestroy() {
//        pwd_diyInputView.clear()
        super.onDestroy()
    }

}
