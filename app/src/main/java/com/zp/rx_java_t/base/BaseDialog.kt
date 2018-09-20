package com.zp.rx_java_t.base

import android.app.Dialog
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.view.*
import com.zp.rx_java_t.util.L
import java.lang.ref.SoftReference

/**
 * com.zp.rx_java_t.base
 * Created by ZP on 2018/9/14.
 * description: 如果是复杂的逻辑，直接继承BaseDialog，否则普通简单的直接使用BuilderDialog create 出
 * version: 1.0
 */
abstract class BaseDialog<D : BaseDialog<D>?> : DialogFragment() {

    private var dialogW = 0
    private var dialogH = 0
    private var dialogCancelable = true
    private var dialogStyle = 0
    private var dialogAnim = 0
    private var dialogGravity = Gravity.CENTER
    private var dialogTag = "BaseDialogTag"
    private var sf: SoftReference<FragmentManager>? = null
    private var dismissLogic: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = getContentView()
        if (contentView <= 0) throw NullPointerException("DialogFragment contentView is not null")
        return inflater.inflate(contentView, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init(view, savedInstanceState)
        dialog.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN)
                onBackPressed()
            else false
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?) :Dialog {
        return if (dialogStyle <= 0) getDialog(savedInstanceState)
        else getDialog(savedInstanceState, dialogStyle)
    }

    abstract fun getContentView(): Int
    abstract fun init(view: View, savedInstanceState: Bundle?)
    abstract fun getDialog(savedInstanceState: Bundle?, style: Int = 0): Dialog
    /**
     * 子类是否使用父类的 onStart里面的方法
     */
    protected open fun getUserFatherStart() = true

    /**
     * 得到屏幕的宽或高
     * @param isWidth   true---宽度；false---高度
     */
    private fun getWidthOrHeight(isWidth: Boolean): Int {
        val manager = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        manager.defaultDisplay.getSize(point)
        return if (isWidth) point.x else point.y
    }

    override fun onStart() {
        super.onStart()
        if (getUserFatherStart()) {
            if (dialogW == 0) {
                dialogW = (getWidthOrHeight(true) * 0.95f).toInt()
            }
            if (dialogH == 0) {
                dialogH = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            dialog.window?.setLayout(dialogW, dialogH)
            if (dialogAnim > 0) dialog.window?.setWindowAnimations(dialogAnim)
            dialog.setCancelable(dialogCancelable)
        }
    }

    fun setDialogCancelable(dialogCancelable: Boolean): D {
        this.dialogCancelable = dialogCancelable
        return getThis()
    }

    protected fun getDialogCancelable() = dialogCancelable

    fun setDialogFM(fm: FragmentManager): D {
        sf = SoftReference(fm)
        return getThis()
    }

    protected fun getDialogFM() = sf?.get()

    fun setDialogStyle(dialogStyle: Int): D {
        this.dialogStyle = dialogStyle
        return getThis()
    }

    protected fun getDialogStyle() = dialogStyle

    fun setDialogAnim(dialogAnim: Int): D {
        this.dialogAnim = dialogAnim
        return getThis()
    }

    protected fun getDialogAnim() = dialogAnim

    fun setDialogGravity(dialogGravity: Int): D {
        this.dialogGravity = dialogGravity
        return getThis()
    }

    protected fun getDialogGravity() = dialogGravity

    fun setDialogWH(dialogW: Int, dialogH: Int): D {
        this.dialogW = dialogW
        this.dialogH = dialogH
        return getThis()
    }

    protected fun getDialogWH() = arrayOf(dialogW, dialogH)

    fun setDialogTag(dialogTag: String): D {
        this.dialogTag = dialogTag
        return getThis()
    }

    protected fun getDialogTag() = dialogTag

    /**
     * 处理销毁逻辑
     */
    fun setDismissLogic(dismissLogic: (() -> Unit)?): D {
        this.dismissLogic = dismissLogic
        return getThis()
    }

    fun show() {
        val fm = sf?.get()
        if (fm != null) {
            val fragment = fm.findFragmentByTag(dialogTag)
            if (fragment != null) fm.beginTransaction().remove(fragment).commit()
            show(fm, dialogTag)
        }
        else L.e("setDialogFM() 未执行？！")
    }

    @Suppress("UNCHECKED_CAST")
    private fun getThis() = this as D

    /**
     * 返回true 拦截，否则销毁
     */
    open fun onBackPressed() = false

    override fun onDestroyView() {
        super.onDestroyView()
        dismissLogic?.invoke()
        destroyAll()
    }

    open fun destroyAll() = Unit

}