package com.zp.rx_java_t.base

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import com.zp.rx_java_t.content.getTextValue
import com.zp.rx_java_t.util.system.L
import java.lang.ref.SoftReference

/**
 * com.zp.rx_java_t.base
 * Created by ZP on 2018/9/25.
 * description:
 * version: 1.0
 */
class SuperSysDialog private constructor(context: Context) {

    private var title = ""
    private var message: Any = ""
    private var cancelable = true

    private var items: Array<String>? = null

    private var singleBlock: ((DialogInterface, Int) -> Unit)? = null
    // Dialog ，item 下标 ， 选中true;else false
    private var multiBlock: ((DialogInterface, Int, Boolean) -> Unit)? = null

    private var isItems = false
    private var isSingle = false
    private var isMulti = false

    // 单选的 选中下标
    private var singleSelectIndex = -1
    // 多选的 选中的数组
    private var multcheckedItems: BooleanArray? = null

    private var positiveText = ""
    private var positiveBlock: ((DialogInterface, Int) -> Unit)? = null

    private var negativeText = ""
    private var negativeBlock: ((DialogInterface, Int) -> Unit)? = null

    private var neutralText = ""
    private var neutralBlock: ((DialogInterface, Int) -> Unit)? = null

    private val reference: SoftReference<Context> by lazy { SoftReference(context) }

    companion object {
        fun create(context: Context) = SuperSysDialog(context)
    }

    fun setTitle(title: String): SuperSysDialog {
        this.title = title
        return this
    }

    fun setMessage(message: Any): SuperSysDialog {
        this.message = message
        return this
    }

    fun setCancelable(cancelable: Boolean): SuperSysDialog {
        this.cancelable = cancelable
        return this
    }

    // 设置列表
    fun setItems(items: Array<String>, singleBlock: (DialogInterface, Int) -> Unit): SuperSysDialog {
        this.singleBlock = singleBlock
        this.items = items
        isItems = true
        isSingle = false
        isMulti = false
        return this
    }

    // 设置单选列表
    fun setSingleItems(items: Array<String>, singleBlock: (DialogInterface, Int) -> Unit): SuperSysDialog {
        this.items = items
        this.singleBlock = singleBlock
        isItems = false
        isSingle = true
        isMulti = false
        return this
    }

    // 设置多选列表
    fun setMultiChoiceItems(items: Array<String>, multiBlock: (DialogInterface, Int, Boolean) -> Unit): SuperSysDialog {
        this.items = items
        this.multiBlock = multiBlock
        isItems = false
        isSingle = false
        isMulti = true
        return this
    }

    // 设置单选的选中的下标
    fun setSingleSelectIndex(singleSelectIndex: Int): SuperSysDialog {
        this.singleSelectIndex = singleSelectIndex
        return this
    }

    // 设置多选的选中的数组
    fun setMultcheckedItems(multcheckedItems: BooleanArray): SuperSysDialog {
        this.multcheckedItems = multcheckedItems
        return this
    }

    fun setPositiveButton(positiveText: String, positiveBlock: (DialogInterface, Int) -> Unit): SuperSysDialog {
        this.positiveText = positiveText
        this.positiveBlock = positiveBlock
        return this
    }

    fun setNegativeButton(negativeText: String, negativeBlock: (DialogInterface, Int) -> Unit): SuperSysDialog {
        this.negativeText = negativeText
        this.negativeBlock = negativeBlock
        return this
    }

    fun setNeutralButton(neutralText: String, neutralBlock: (DialogInterface, Int) -> Unit): SuperSysDialog {
        this.neutralText = neutralText
        this.neutralBlock = neutralBlock
        return this
    }

    fun builder() {
        if (reference.get() != null) {
            AlertDialog.Builder(reference.get()!!).apply {
                setTitle(if (title.isEmpty()) "温馨提示" else title)
                if (getTextValue(message).isNotEmpty()) setMessage(getTextValue(message))
                setCancelable(cancelable)
                if (isItems) {
                    setItems(items) { d, i -> singleBlock?.invoke(d, i) }
                }
                if (isSingle) {
                    setSingleChoiceItems(items, singleSelectIndex) { d, i -> singleBlock?.invoke(d, i)}
                }
                if (isMulti) {
                    setMultiChoiceItems(items, multcheckedItems) { d, i, f -> multiBlock?.invoke(d, i, f) }
                }
                if (positiveBlock != null && positiveText.isNotEmpty()) {
                    setPositiveButton(positiveText) { d, i -> positiveBlock?.invoke(d, i) }
                }
                if (negativeBlock != null && negativeText.isNotEmpty()) {
                    setNegativeButton(negativeText) { d, i -> negativeBlock?.invoke(d, i) }
                }
                if (neutralBlock != null && neutralText.isNotEmpty()) {
                    setNeutralButton(neutralText) { d, i -> neutralBlock?.invoke(d, i) }
                }
                setOnDismissListener {
                    items = null
                    multcheckedItems = null
                    singleBlock = null
                    multiBlock = null
                    positiveBlock = null
                    negativeBlock = null
                    neutralBlock = null
                    reference.clear()
                }
                show()
            }
        } else L.e("Context is null")
    }

}