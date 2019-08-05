package com.zp.rx_java_t.util.file

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider
import com.zp.rx_java_t.bean.LocalFileBean
import com.zp.rx_java_t.content.Content
import com.zp.rx_java_t.content.IS_OFFICIAL
import com.zp.rx_java_t.content.showToast
import java.io.File

object FileOpenUtil {

    // txt
    private const val TXT_TYPE = "text/plain"
    // zip
    private const val ZIP_TYPE = "application/x-zip-compressed"
    // word
    private const val DOC_TYPE = "application/msword"
    // excel
    private const val XLS_TYPE = "application/vnd.ms-excel"
    // ppt
    private const val PPT_TYPE = "application/vnd.ms-powerpoint"
    // pdf
    private const val PDF_TYPE = "application/pdf"

    private const val ERROR_MSG = "文件类型不匹配或找不到打开该文件类型的程序，打开失败"

    /**
     * 打开文件
     */
    fun openFile(item: LocalFileBean?, context: Context) {
        if (item == null) return
        when (item.type.toLowerCase()) {
            Content.TXT -> openTXT(item.path, context)
            Content.DOC, Content.DOCX -> openDOC(item.path, context)
            Content.XLS, Content.XLSX -> openXLS(item.path, context)
            Content.PPT, Content.PPTX -> openPPT(item.path, context)
            Content.PDF -> openPDF(item.path, context)
            else -> showToast(ERROR_MSG)
        }
    }

    private fun openTXT(filePath: String, context: Context) {
        open(filePath, context, TXT_TYPE)
    }

    private fun openZIP(filePath: String, context: Context) {
        open(filePath, context, ZIP_TYPE)
    }

    private fun openDOC(filePath: String, context: Context) {
        open(filePath, context, DOC_TYPE)
    }

    private fun openXLS(filePath: String, context: Context) {
        open(filePath, context, XLS_TYPE)
    }

    private fun openPPT(filePath: String, context: Context) {
        open(filePath, context, PPT_TYPE)
    }

    private fun openPDF(filePath: String, context: Context) {
        open(filePath, context, PDF_TYPE)
    }

    private fun open(filePath: String, context: Context, type: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW).apply {
                addCategory("android.intent.category.DEFAULT")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val contentUri = FileProvider.getUriForFile(context,
                            "${context.packageName}.FileProvider", File(filePath)
                    )
                    setDataAndType(contentUri, type)
                } else {
                    val uri = Uri.fromFile(File(filePath))
                    setDataAndType(uri, type)
                }
            })
        } catch (e: Exception) {
            if (!IS_OFFICIAL) e.printStackTrace()
            showToast(ERROR_MSG)
        }
    }

}