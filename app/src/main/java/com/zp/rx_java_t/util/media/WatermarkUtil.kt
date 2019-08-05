package com.zp.rx_java_t.util.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import com.zp.rx_java_t.content.getAppContext
import com.zp.rx_java_t.content.getDisplay
import com.zp.rx_java_t.util.system.L
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 * 水印相关
 */
object WatermarkUtil {

    /** 左上角 */
    const val WATERMARK_LEFT_TOP = 0x1000
    /** 左下角 */
    const val WATERMARK_LEFT_BOTTOM = 0x1001
    /** 右上角 */
    const val WATERMARK_RIGHT_TOP = 0x1002
    /** 右下角 */
    const val WATERMARK_RIGHT_BOTTOM = 0x1003
    /** 中间 */
    const val WATERMARK_CENTER = 0x1004

    /** 水印默认的宽   高根据宽自适应 */
    private const val DEFAULT_WATERMARK_WIDTH = 200

    /**
     * 根据屏幕、视频的宽高计算水印的宽高、位置
     * 该方法可以使水印在任何视频、手机分辨率上大小保持一致
     *
     * @param videoWidth        视频宽
     * @param videoHeight       视频高
     * @param watermarkWidth    原水印宽
     * @param watermarkHeight   原水印高
     *
     * @return Array<Float> [0] = 为计算出来的比例 ,[1] = 水印实际的高【PS：宽是固定的】
     * 使用方式 ：各项 / [0]比例 即可 参考 addWatermarkToVideo 方法
     */
    fun calWatermarkByDisplay(videoWidth: Float, videoHeight: Float, watermarkWidth: Float,
                              watermarkHeight: Float): Array<Float> {
        val array = arrayOf(-1f, -1f)
        val displayArray = getAppContext().getDisplay()
        // 屏幕的宽
        val displayWidth = displayArray[0]
        // 屏幕的高
        val displayHeight = displayArray[1]
        // 先计算屏幕的宽高与视频宽高的比，最后取最小值
        array[0] = Math.min(displayWidth / videoWidth, displayHeight / videoHeight)
        // 根据水印的宽度 计算出水印高度
        array[1] = (DEFAULT_WATERMARK_WIDTH * watermarkHeight) / watermarkWidth
        return array
    }

    /**
     * 视频添加水印
     * @param watermarkPath     水印路径
     * @param array             水印所需参数，空使用默认值 参考 calWatermarkByDisplay 方法
     * @param videoWidth        视频宽
     * @param videoHeight       视频高
     * @param padding           水印边距
     * @param watermarkLocation 水印位置
     * @param isGif             水印是否是Gif
     */
    fun addWatermarkToVideo(watermarkPath: String, array: Array<Float>?, videoWidth: Int, videoHeight: Int, padding: Int = 10,
                            watermarkLocation: Int = WATERMARK_LEFT_BOTTOM, isGif: Boolean = false): Any {
        var thisArray = array
        if (thisArray == null || thisArray.isEmpty()) {
            val bitmap = BitmapFactory.decodeFile(watermarkPath)
            thisArray = calWatermarkByDisplay(videoWidth.toFloat(), videoHeight.toFloat(),
                    bitmap.width.toFloat(), bitmap.height.toFloat())
            bitmap.recycle()
        }

        // 屏幕的宽高与视频宽高的比
        val scale = thisArray[0]
        val watermarkWidth = DEFAULT_WATERMARK_WIDTH
        // 水印高度
        val watermarkHeight = thisArray[1]

        // 水印在视频上的实际宽，高
        val watermarkW = watermarkWidth / scale
        val watermarkH = watermarkHeight / scale
        // padding 在视频上的实际值
        val xy = (padding / scale).toInt()

        return when (watermarkLocation) {
            WATERMARK_LEFT_TOP -> {
//                EpDraw(watermarkPath, xy, xy, watermarkW, watermarkH, isGif)
            }
            WATERMARK_LEFT_BOTTOM -> {
//                EpDraw(watermarkPath, xy, (videoHeight - watermarkH - padding).toInt(),
//                        watermarkW, watermarkH, isGif)
            }
            WATERMARK_RIGHT_TOP -> {
//                EpDraw(watermarkPath, (videoWidth - watermarkW - padding).toInt(), xy,
//                        watermarkW, watermarkH, isGif)
            }
            WATERMARK_RIGHT_BOTTOM -> {
//                EpDraw(watermarkPath, (videoWidth - watermarkW - padding).toInt(),
//                        (videoHeight - watermarkH - padding).toInt(), watermarkW, watermarkH, isGif)
            }
            else -> {
                throw IllegalAccessException("\"watermarkLocation\" -> Parameters of illegal")
            }
        }
    }

    /**
     * 图片添加水印
     * @param block             参数回调
     * @param picPath           图片路径
     * @param watermarkPath     水印路径
     * @param watermarkWidth    水印宽 -> 0表示根据图片宽自适应
     * @param watermarkHeight   水印高 -> 0表示根据图片高自适应
     * @param watermarkLocation 水印位置
     * @param padding           水印边距 -> 0表示根据图片宽高自适应
     */
    fun addWatermarkToPic(block: (String?, Boolean) -> Unit, picPath: String, watermarkPath: String,
                          watermarkWidth: Float = 0f, watermarkHeight: Float = 0f,
                          watermarkLocation: Int = WATERMARK_LEFT_BOTTOM, padding: Int = 0) {
        // 水印图片
        var watermarBitmap = BitmapUtil.getBitmapForFile(watermarkPath, 0, 0)

        // 原图
        val picBitmap = BitmapUtil.getBitmapForFile(picPath, 0, 0)
        val width = picBitmap.width
        val height = picBitmap.height

        if (width <= 50 || height <= 50) {
            picBitmap.recycle()
            L.e("图片宽度或高度小于50，直接返回原图")
            block(null, false)
        }

        // 创建一个新的和 原图 长度宽度一样的位图
        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        //将该图片作为画布
        val canvas = Canvas(newBitmap)
        //在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(picBitmap, 0f, 0f, null)

        // 修改水印图片宽高 根据图片 缩放至0.12的比例进行压缩
        val scale = width * 0.12f / watermarBitmap.width

        val matrix = Matrix()
        val scaleX = if (watermarkWidth == 0f) scale
        else watermarkWidth / watermarBitmap.width.toFloat()

        val scaleY = if (watermarkHeight == 0f) scale
        else watermarkHeight / watermarBitmap.height.toFloat()

        matrix.setScale(scaleX, scaleY)
        watermarBitmap = Bitmap.createBitmap(watermarBitmap, 0, 0,
                watermarBitmap.width, watermarBitmap.height, matrix, true)

        val autoPadding = if (padding == 0) width * 0.03f else 20f

        //在画布上绘制水印图片
        when (watermarkLocation) {
            WATERMARK_LEFT_TOP -> {
                canvas.drawBitmap(watermarBitmap, autoPadding, autoPadding, null)
            }
            WATERMARK_LEFT_BOTTOM -> {
                val top = height - watermarBitmap.height - autoPadding
                canvas.drawBitmap(watermarBitmap, autoPadding, top, null)

            }
            WATERMARK_RIGHT_TOP -> {
                val left = width - watermarBitmap.width - autoPadding
                canvas.drawBitmap(watermarBitmap, left, autoPadding, null)
            }
            WATERMARK_RIGHT_BOTTOM -> {
                val left = width - watermarBitmap.width - autoPadding
                val top = height - watermarBitmap.height - autoPadding
                canvas.drawBitmap(watermarBitmap, left, top, null)
            }
            WATERMARK_CENTER -> {
                val left = width / 2 - watermarBitmap.width / 2
                val top = height / 2 - watermarBitmap.height / 2
                canvas.drawBitmap(watermarBitmap, left.toFloat(), top.toFloat(), null)
            }
        }
        canvas.save()
        canvas.restore()

        // 保存图片
        BitmapUtil.saveBitmap(newBitmap, "", Bitmap.CompressFormat.JPEG, block)
    }

    /**
     * 从assets目录中复制文件到SD卡
     * @param  context  Context 使用CopyFiles类的Activity
     * @param  oldPath  String  原文件夹路径  如：/aa
     * @param  newPath  String  复制后路径  如：xx:/bb/cc
     */
    fun copyAssetsWatermarkFileToSDCard(context: Context, oldPath: String, newPath: String) {
        var inputStream: InputStream? = null
        var fos: FileOutputStream? = null
        Thread {
            try {
                // 获取assets目录下的所有文件及目录名
                val fileNames = context.assets.list(oldPath)
                if (fileNames.isNotEmpty()) { //如果是目录
                    val file = File(newPath)
                    file.mkdirs() //如果文件夹不存在，则递归
                    for (fileName in fileNames) {
                        copyAssetsWatermarkFileToSDCard(context, "$oldPath/$fileName", "$newPath/$fileName")
                    }
                } else { // 如果是文件
                    inputStream = context.assets.open(oldPath)
                    val fileName = oldPath.substring(oldPath.lastIndexOf("/") + 1, oldPath.length)
                    L.e("赋值到本地SD卡的文件为：$fileName")
                    fos = FileOutputStream(File(newPath))
                    val buffer = ByteArray(1024)
                    while (true) {
                        val byteCount = inputStream!!.read(buffer)
                        if (byteCount == -1) {
                            break
                        } else {
                            fos?.write(buffer, 0, byteCount)
                        }
                    }
                    // 刷新缓冲区
                    fos?.flush()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                inputStream?.close()
                fos?.close()
            }
        }.start()
    }

}