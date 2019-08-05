package com.zp.rx_java_t.util.file

import android.content.Context
import android.database.Cursor
import android.os.AsyncTask
import android.provider.MediaStore
import com.zp.rx_java_t.bean.LocalFileBean
import com.zp.rx_java_t.content.Content.DOC
import com.zp.rx_java_t.content.Content.DOCX
import com.zp.rx_java_t.content.Content.PDF
import com.zp.rx_java_t.content.Content.PPT
import com.zp.rx_java_t.content.Content.PPTX
import com.zp.rx_java_t.content.Content.TXT
import com.zp.rx_java_t.content.Content.XLS
import com.zp.rx_java_t.content.Content.XLSX
import com.zp.rx_java_t.content.IS_OFFICIAL
import java.lang.Exception
import java.lang.StringBuilder
import java.lang.ref.SoftReference
import java.util.ArrayList

/**
 * 获取手机 所有 txt、doc、xls、ppt、pdf 文件（或其他文件）
 */
class FileAsync(
        context: Context,
        private var block: (ArrayList<LocalFileBean>?) -> Unit,
        private var extension: Array<String> = arrayOf(TXT, DOC, DOCX, XLS, XLSX, PPT, PPTX, PDF)
) : AsyncTask<Void, Void, ArrayList<LocalFileBean>?>() {

    private val soft by lazy {
        SoftReference<Context>(context)
    }

    override fun doInBackground(vararg params: Void?) = getLocalData()

    override fun onPostExecute(result: ArrayList<LocalFileBean>?) {
        soft.clear()
        block.invoke(result)
    }

    private fun getLocalData() = ArrayList<LocalFileBean>().apply {
        var cursor: Cursor? = null
        try {
            val context = soft.get() ?: return this
            // 从SD卡中获取
            val fileUri = MediaStore.Files.getContentUri("external")
            // 查询指定的列
            val projection = arrayOf(
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.TITLE,
                    MediaStore.Files.FileColumns.SIZE,
                    MediaStore.Files.FileColumns.DATE_MODIFIED
            )
            val sb = StringBuilder()
            extension.forEach {
                if (it == extension[extension.size - 1]) {
                    sb.append(MediaStore.Files.FileColumns.DATA).append(" LIKE '%.$it'")
                } else {
                    sb.append(MediaStore.Files.FileColumns.DATA).append(" LIKE '%.$it' OR ")
                }
            }
            // 构造筛选语句
            val selection = sb.toString()
            // 排序方式
            val sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED
            // 获取内容解析器对象
            val resolver = context.contentResolver
            cursor = resolver.query(fileUri, projection, selection, null, sortOrder) ?: return this
            if (cursor.moveToLast()) {
                do {
                    val path = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                    val size = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE))
                    val date = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED))
                    val fileSize = MyFileUtil.formetFileSize(size)
                    if (fileSize > 0.0) {
                        val name = path.substring(path.lastIndexOf("/") + 1, path.length)
                        val type = path.substring(path.lastIndexOf(".") + 1, path.length)
                        add(LocalFileBean(name, path, date, type, fileSize))
                    }
                } while (cursor.moveToPrevious())
            }
        } catch (e: Exception) {
            if (!IS_OFFICIAL) e.printStackTrace()
        } finally {
            cursor?.close()
        }
    }
}