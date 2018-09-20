package com.zp.rx_java_t.base

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup

/**
 * com.zp.rx_java_t.base
 * Created by ZP on 2018/9/14.
 * description: 普通简单的直接使用BuilderDialog create 出
 * version: 1.0
 */
class BuilderDialog : BaseDialog<BuilderDialog>() {

    private var contentView = 0
    private var logic: ((View, Dialog) -> Unit)? = null

    companion object {

        const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT
        const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT

        fun create() = BuilderDialog()
    }

    override fun getContentView() = contentView

    override fun init(view: View, savedInstanceState: Bundle?) {
        logic?.invoke(view, dialog)
    }

    override fun getDialog(savedInstanceState: Bundle?, style: Int) =
            Dialog(context, style).apply {
                window.setGravity(getDialogGravity())
            }

    fun setContentView(contentView: Int): BuilderDialog {
        this.contentView = contentView
        return this
    }

    /**
     * 处理初始化的逻辑
     */
    fun setInitLogic(logic: ((View, Dialog) -> Unit)?): BuilderDialog {
        this.logic = logic
        return this
    }
}