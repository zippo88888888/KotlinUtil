package com.zp.rx_java_t.base

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.zp.rx_java_t.content.getTextValue
import java.io.File

/**
 * com.zp.rx_java_t.base
 * Created by ZP on 2018/9/21.
 * description:
 * version: 1.0
 */
class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var arrayView = SparseArray<View>()

    fun <V : View> getView(id :Int): V {
        var view = arrayView[id]
        if (view == null) {
            view = itemView.findViewById(id)
            arrayView.put(id, view)
        }
        return view as V
    }

    fun setTextValue(id: Int, str: Any) {
        getView<TextView>(id).text = getTextValue(str)
    }

    fun setImageValue(id: Int, source: Any) {
        val s = if (source is Int || source is String || source is File) source
        else throw IllegalAccessException("source is not Int、String or File")
        getView<ImageView>(id).apply {
            Glide.with(itemView.context).load(s).into(this)
        }
    }

    fun setVisibility(id: Int, isShow: Boolean) {
        setVisibility(id, if (isShow) View.VISIBLE else View.GONE)
    }

    fun setVisibility(id: Int, visibility: Int) {
        getView<View>(id).visibility = visibility
    }

    fun setOnClickListener(id: Int, listener: (View) -> Unit) {
        getView<View>(id).setOnClickListener { listener(it) }
    }

    fun setOnItemClickListener(position: Int, block: (View, Int) -> Unit) {
        itemView.setOnClickListener { block(it, position) }
    }

    fun setOnItemLongClickListener(position: Int, block: (View, Int) -> Boolean) {
        itemView.setOnLongClickListener { block(it, position) }
    }

    fun setOnTouchListener(position: Int, block: (View, MotionEvent, Int) -> Boolean) {
        itemView.setOnTouchListener { v, event -> block(v, event, position) }
    }
}