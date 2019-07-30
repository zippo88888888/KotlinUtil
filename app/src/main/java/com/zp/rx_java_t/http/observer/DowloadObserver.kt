package com.zp.rx_java_t.http.observer

import okhttp3.ResponseBody

/**
 * 文件下载
 */

class DowloadObserver(private var fileName: String, private var fileDir: String) : BaseObserver<ResponseBody>() {

    override fun doOnNext(bean: ResponseBody) {

    }
}