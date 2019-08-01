package com.zp.rx_java_t.http.observer

import android.os.Handler
import android.os.Message
import com.zp.rx_java_t.util.L
import okhttp3.ResponseBody
import java.io.*
import java.lang.ref.WeakReference

/**
 * 文件下载  https://www.jianshu.com/p/2659bbe0df16
 * @property fileDir String     文件目录
 * @property fileName String    文件名称
 */
abstract class DowloadObserver(private var fileDir: String, private var fileName: String) : BaseObserver<ResponseBody>() {

    private var handler: DowloadHandler? = null

    override fun doOnNext(bean: ResponseBody) {
        if (handler == null) {
            handler = DowloadHandler(this)
        }
        Thread {
            startDownloadFile(bean)
        }.start()
    }

    final override fun doOnError(errorMsg: String?, errorCode: Int) {
        super.doOnError(errorMsg, errorCode)
        dowloadFailed()
    }

    private fun startDownloadFile(body: ResponseBody) {
        val file = fileDir + fileName
        val dowloadFile = File(file)
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        try {
            val fileReader = ByteArray(4096)
            // 文件总大小
            val fileSize = body.contentLength()
            // 当前已下载的大小
            var fileSizeDownloaded = 0L

            inputStream = body.byteStream()
            outputStream = FileOutputStream(dowloadFile)
            while (true) {
                val read = inputStream!!.read(fileReader)
                if (read == -1) {
                    handler?.sendMessage(Message().apply {
                        what = DowloadHandler.DOWLOAD_SUCCESS
                        obj = dowloadFile
                    })
                    break
                }
                outputStream.write(fileReader, 0, read)

                fileSizeDownloaded += read.toLong()
                handler?.sendMessage(Message().apply {
                    what = DowloadHandler.DOWLOADING
                    obj = arrayOf(fileSizeDownloaded, fileSize)
                })
            }
            outputStream.flush()
        } catch (e: IOException) {
            e.printStackTrace()
            handler?.sendEmptyMessage(DowloadHandler.DOWLOAD_FAILED)
        } finally {
            inputStream?.close()
            outputStream?.close()
        }

    }

    abstract fun dowloadSuccess(file: File)

    abstract fun dowloadFailed()

    /**
     * 进度
     * @param byteDowload Long  已下载的文件大小(byte)
     * @param fileSize Long     文件大小(byte)
     * @param progress Float    当前下载进度
     */
    open fun progress(byteDowload: Long, fileSize: Long, progress: Float) = Unit

    private class DowloadHandler(dowloadObserver: DowloadObserver) : Handler() {

        companion object {
            const val DOWLOADING = 1
            const val DOWLOAD_SUCCESS = 2
            const val DOWLOAD_FAILED = 3
        }

        private val weakReference by lazy { WeakReference<DowloadObserver>(dowloadObserver) }

        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                DOWLOADING -> {
                    val array = msg.obj as Array<Long>
                    val fileSizeDownloaded = array[0]
                    val fileSize = array[1]
                    weakReference.get()?.progress(fileSizeDownloaded, fileSize,
                            (fileSizeDownloaded.toFloat() / fileSize.toFloat()) * 100F)
                }
                DOWLOAD_SUCCESS -> {
                    val file = msg.obj as File
                    weakReference.get()?.dowloadSuccess(file)
                }
                else -> {
                    weakReference.get()?.dowloadFailed()
                }

            }
        }
    }
}