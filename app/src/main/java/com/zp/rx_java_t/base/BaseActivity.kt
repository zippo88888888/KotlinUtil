package com.zp.rx_java_t.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.layout_tool_bar.*

/**
 * com.zp.rx_java_t.base
 * Created by ZP on 2018/9/20.
 * description:
 * version: 1.0
 */
abstract class BaseActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentView())
        if (getBarState()) {
            setBar()
        }
        init(savedInstanceState)
    }

    protected open fun getBarState() = true

    abstract fun getContentView(): Int
    abstract fun init(savedInstanceState: Bundle?)

    override fun onClick(v: View?) {

    }

    private fun setBar() {
        setBarTitle("")
        setSupportActionBar(tool_bar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun setBarTitle(title: String) {
        tool_bar.title = title
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            back()
        }
        return super.onOptionsItemSelected(item)
    }

    protected open fun back() {
        onBackPressed()
    }

}