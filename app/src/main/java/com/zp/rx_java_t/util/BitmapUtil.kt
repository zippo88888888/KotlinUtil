package com.zp.rx_java_t.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import com.zp.rx_java_t.content.IS_OFFICIAL
import java.io.FileOutputStream

/**
 * bitmap相关
 */
object BitmapUtil {

    private fun computeSampleSize(options: BitmapFactory.Options, minSideLength: Int, maxNumOfPixels: Int): Int {
        val initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels)
        var roundedSize: Int
        if (initialSize <= 8) {
            roundedSize = 1
            while (roundedSize < initialSize) {
                roundedSize = roundedSize shl 1
            }
        } else roundedSize = (initialSize + 7) / 8 * 8
        if (Math.max(options.outWidth, options.outHeight) / roundedSize < minSideLength) {
            roundedSize = roundedSize shr 1
        }
        return roundedSize
    }

    private fun computeInitialSampleSize(options: BitmapFactory.Options, minSideLength: Int, maxNumOfPixels: Int): Int {
        val w = options.outWidth.toDouble()
        val h = options.outHeight.toDouble()
        val lowerBound = if (maxNumOfPixels < 0) 1 else Math.ceil(Math.sqrt(w * h / maxNumOfPixels)).toInt()
        val upperBound = if (minSideLength < 0) 128 else Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength)).toInt()
        if (upperBound < lowerBound) return lowerBound
        return if (maxNumOfPixels == -1 && minSideLength == -1) 1
        else if (minSideLength == -1) lowerBound
        else upperBound
    }

    /**
     * 获取 一个bitmap 占用内存大小
     */
    fun getMemorySize(bitmap: Bitmap) {
        val config = bitmap.config
        val width = bitmap.width
        val height = bitmap.height
        val totalSize = when (config) {
            Bitmap.Config.ALPHA_8 -> width * height
            Bitmap.Config.ARGB_4444 -> width * height * 2
            Bitmap.Config.RGB_565 -> width * height * 2
            else -> width * height * 4 // ARGB_8888
        }
        L.e("bitmap", "width：$width height：$height <<<===>>> 占用内存约: ${totalSize / 1024} KB")
    }

    /**
     * 根据图片的宽高 得到bitmap
     * @param width     ImageView的宽
     * @param height    ImageView的高
     */
    fun getBitmapForFile(imagePath: String, width: Int, height: Int): Bitmap {
        val map = BitmapFactory.decodeFile(imagePath, BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(imagePath, this)
            inPreferredConfig = Bitmap.Config.RGB_565
            inJustDecodeBounds = false
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                inPurgeable = true
                inInputShareable = true
            }
            inSampleSize = computeSampleSize(this, Math.max(width, height), width * height)
        })
        getMemorySize(map)
        return map
    }

    /**
     * 保存图片
     * @param filename 文件绝对路径
     * @param type     图片类型
     * @param block    由于是子线程，如果需要更新UI 必须UI线程
     */
    fun saveBitmap(bmp: Bitmap, filename: String, type: Bitmap.CompressFormat, block: (String?, Boolean) -> Unit) {
        var fileout: FileOutputStream? = null
        var isSuccess = true
        Thread {
            try {
                fileout = FileOutputStream(filename)
                bmp.compress(type, 100, fileout)
                fileout?.flush()
                L.e("bitmap 保存成功--->>>$filename")
            } catch (e: Exception) {
                L.e("bitmap 保存失败")
                isSuccess = false
                if (!IS_OFFICIAL) e.printStackTrace()
            } finally {
                fileout?.close()
                block(filename, isSuccess)
            }
        }.start()

    }

}