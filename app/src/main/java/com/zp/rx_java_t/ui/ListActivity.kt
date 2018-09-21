package com.zp.rx_java_t.ui

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.zp.rx_java_t.R
import com.zp.rx_java_t.base.BaseActivity
import com.zp.rx_java_t.base.BaseAdapter
import com.zp.rx_java_t.base.BaseViewHolder
import com.zp.rx_java_t.view.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_list.*

class ListActivity : BaseActivity() {

    private var listAdapter: BaseAdapter<String>? = null

    override fun getContentView() = R.layout.activity_list

    override fun init(savedInstanceState: Bundle?) {
        setBarTitle("RecyclerView")
        listAdapter = object : BaseAdapter<String>(this, android.R.layout.simple_list_item_1) {
            override fun bindView(holder: BaseViewHolder, position: Int) {
                holder.setTextValue(android.R.id.text1, getItem(position))
            }
        }
        list_list.apply {
            layoutManager = LinearLayoutManager(this@ListActivity)
            addItemDecoration(RecycleViewDivider(this@ListActivity))
            adapter = listAdapter
        }

        initClick()
    }

    private fun initClick() {
        list_datas.setOnClickListener { listAdapter?.datas = getDatas() }
        list_addAll.setOnClickListener { listAdapter?.addAll(getDatas()) }
        list_clear.setOnClickListener { listAdapter?.clear() }
        list_addItem.setOnClickListener { listAdapter?.addItem(0, "addItem") }
        list_setItem.setOnClickListener { listAdapter?.setItem(0, "setItem") }
        list_delete.setOnClickListener { listAdapter?.remove(0) }
    }

    private fun getDatas() = ArrayList<String>().apply {
        for (i in 0 until 10) {
            add("item$i")
        }
    }

}
