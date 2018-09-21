package com.zp.rx_java_t.base

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * com.zp.rx_java_t.base
 * Created by ZP on 2018/9/21.
 * description:
 * version: 1.0
 */
abstract class BaseAdapter<T>(protected var context: Context) : RecyclerView.Adapter<BaseViewHolder>() {

    private var layoutID = -1
    var datas: ArrayList<T> = ArrayList()
        set(value) {
            if (value.isNotEmpty()) {
                clear()
                field = value
                notifyDataSetChanged()
            }
        }

    constructor(context: Context, layoutID: Int) : this(context) {
        this.layoutID = layoutID
    }

    /** 方便子类重写  datas 方法 */
    open fun setData(data: ArrayList<T>) {
        datas = data
    }

    open fun addAll(data: ArrayList<T>) {
        val size = itemCount
        if (datas.addAll(data)) {
            notifyItemRangeChanged(size, data.size)
        }
    }

    open fun addItem(position: Int, t: T) {
        datas.add(position, t)
        notifyItemInserted(position)
    }

    open fun setItem(position: Int, t: T) {
        if (itemCount <= 0) return
        datas[position] = t
        notifyItemChanged(position)
    }

    open fun remove(position: Int, isDataSetChanged: Boolean = true) {
        if (itemCount <= 0) return
        datas.removeAt(position)
        if (isDataSetChanged) notifyItemRangeRemoved(position, 1)
    }

    open fun remove(t: T, isDataSetChanged: Boolean = true) {
        if (itemCount <= 0) return
        val flag = datas.remove(t)
        if (flag && isDataSetChanged) notifyDataSetChanged()
    }

    open fun clear(isDataSetChanged: Boolean = true) {
        datas.clear()
        if (isDataSetChanged) notifyDataSetChanged()
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layoutRes = getLayoutID(viewType)
        if (layoutRes > 0) {
            val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
            return BaseViewHolder(view)
        } else throw NullPointerException("adapter layoutId is not null")
    }

    final override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        bindView(holder, position)
    }

    final override fun getItemCount() = datas.size

    open fun getItem(position: Int) = datas[position]

    open fun getLayoutID(viewType: Int) = layoutID

    abstract fun bindView(holder: BaseViewHolder, position: Int)

}