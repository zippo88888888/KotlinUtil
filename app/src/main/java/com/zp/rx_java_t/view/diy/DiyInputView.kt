package com.zp.rx_java_t.view.diy

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.inputmethodservice.KeyboardView
import android.util.AttributeSet
import android.view.View
import com.zp.rx_java_t.R
import com.zp.rx_java_t.content.getColorById
import com.zp.rx_java_t.content.getDisplay
import com.zp.rx_java_t.content.getSize
import com.zp.rx_java_t.content.showToast
import com.zp.rx_java_t.util.L
import java.lang.StringBuilder



@SuppressLint("DrawAllocation")
class DiyInputView : View {

    companion object {

        /** 圆 */
        const val FRAME_TXT_HIDDEN_TYPE_CIRCLE = 0x100
        /** 矩形 */
        const val FRAME_TXT_HIDDEN_TYPE_RECTANGLE = 0x200
        /** 三角形 */
        const val FRAME_TXT_HIDDEN_TYPE_TRIANGLE = 0x300

        /** 默认 框 未输入的颜色 */
        private const val DEFAULT_FRAME_COLOR = R.color.black
        /** 默认 框 输入后的颜色 */
        private const val DEFAULT_FRAME_COLOR2 = R.color.green
        /** 默认 框 宽度 */
        private const val DEFAULT_FRAME_WIDTH = 5f
        /** 默认 框 圆角大小 */
        private const val DEFAULT_FRAME_RADIUS = 5f
        /** 默认 框 个数 */
        private const val DEFAULT_FRAME_LENGTH = 6
        /** 默认 框 间距 */
        private const val DEFAULT_FRAME_PADDING = 20f

        /** 默认 文字 输入后的颜色 */
        private const val DEFAULT_FRAME_TXT_COLOR2 = DEFAULT_FRAME_COLOR2
        /** 默认 文字 大小 */
        private const val DEFAULT_FRAME_TXT_SIZE = 50f

        /** 默认 文字 隐藏后的宽度 */
        private const val DEFAULT_FRAME_TXT_HIDDEN_WIDTH = 30f
        /** 默认 文字 隐藏的样式 */
        private const val DEFAULT_FRAME_TXT_HIDDEN_TYPE = FRAME_TXT_HIDDEN_TYPE_CIRCLE

    }

    interface InputValueListener {

        // 正在输入
        fun input(inputValue: String, value: String)

        // 输入完成
        fun inputEnd(value: String)

    }

    // 每个框的实际宽
    private var iFrameWidth = 0
    // 每个框的实际高
    private var iFrameHeight = 0

    var inputValueListener: InputValueListener? = null

    // 框 未输入的颜色
    var frameColor = 0
    // 框 输入后的颜色
    var frameColor2 = 0
    // 框 宽度
    var frameWidth = 0f
    // 框 圆角大小
    var frameRadius = 0f
    // 框 长度
    var frameLength = 0
    // 框 间距
    var framePadding = 0f

    // 文字 输入的颜色
    var frameTxtColor = 0
    // 文字 大小
    var frameTxtSize = 0f

    // 文字输入后是否隐藏
    var frameTxtHidden = false
        set(value) {
            field = value
            invalidate()
        }
    // 文字 隐藏后的宽度（半径）
    var frameTxtHiddenWidth = 0f
    // 文字 隐藏的样式
    var frameTxtHiddenType = 0
        set(value) {
            field = value
            invalidate()
        }

    private lateinit var framePaint: Paint
    private lateinit var txtPaint: Paint
    private lateinit var pwdPaint: Paint

    private var keyBoardUtil: KeyBoardUtil? = null
    private var keyBoardView: KeyboardView? = null

    private lateinit var texts: ArrayList<String>
    // 当前输入的下标
    private var textIndex = 0

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val array = context!!.obtainStyledAttributes(attrs, R.styleable.DiyInputView)
        frameColor = array.getColor(R.styleable.DiyInputView_iFrameColor, getColorById(DEFAULT_FRAME_COLOR))
        frameColor2 = array.getColor(R.styleable.DiyInputView_iFrameColor2, getColorById(DEFAULT_FRAME_COLOR2))
        frameWidth = array.getDimension(R.styleable.DiyInputView_iFrameWidth, DEFAULT_FRAME_WIDTH)
        frameRadius = array.getDimension(R.styleable.DiyInputView_iFrameRadius, DEFAULT_FRAME_RADIUS)
        frameLength = array.getInteger(R.styleable.DiyInputView_iFrameLength, DEFAULT_FRAME_LENGTH)
        framePadding = array.getDimension(R.styleable.DiyInputView_iFramePadding, DEFAULT_FRAME_PADDING)
        frameTxtColor = array.getColor(R.styleable.DiyInputView_iFrameTxtColor, getColorById(DEFAULT_FRAME_TXT_COLOR2))
        frameTxtSize = array.getDimension(R.styleable.DiyInputView_iFrameTxtSize, DEFAULT_FRAME_TXT_SIZE)
        frameTxtHidden = array.getBoolean(R.styleable.DiyInputView_iFrameTxtHidden, false)
        frameTxtHiddenWidth = array.getDimension(R.styleable.DiyInputView_iFrameTxtHiddenWidth, DEFAULT_FRAME_TXT_HIDDEN_WIDTH)
        frameTxtHiddenType = array.getInteger(R.styleable.DiyInputView_iFrameTxtHiddenType, DEFAULT_FRAME_TXT_HIDDEN_TYPE)
        array.recycle()
        init()
    }

    private fun init() {
        if (frameLength <= 0) frameLength = DEFAULT_FRAME_LENGTH
        texts = ArrayList(frameLength)

        framePaint = Paint(Paint.ANTI_ALIAS_FLAG).run {
            this.style = Paint.Style.STROKE
            this.color = frameColor
            this.strokeWidth = frameWidth
            this
        }
        txtPaint = Paint(Paint.ANTI_ALIAS_FLAG).run {
            this.color = frameTxtColor
            this.textSize = frameTxtSize
            this
        }
        pwdPaint = Paint(Paint.ANTI_ALIAS_FLAG).run {
            this.color = frameTxtColor
            this.style = Paint.Style.FILL
            this
        }
        keyBoardUtil = KeyBoardUtil(context)
        keyBoardUtil?.keyBoardOutPutListener = { checkInput(it)}
    }

    fun bindBoardView(keyBoardView: KeyboardView) {
        this.keyBoardView = keyBoardView
        keyBoardUtil?.showKeyBoard(keyBoardView)
    }

    // 获取输入的文字
    private fun getText(): String {
        val sb = StringBuilder()
        texts.forEach {
            if (it.isNotEmpty()) sb.append(it)
        }
        return sb.toString()
    }

    // 是否输入完成
    private fun isInputDown() = textIndex == frameLength


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = getSize(context.getDisplay()[0], widthMeasureSpec)
        iFrameWidth = ((width - (frameLength + 1) * framePadding) / frameLength).toInt()
        // 判断高度  如果控件高度不指定 iFrameHeight = iFrameWidth 否则就计算出与控件的高度的值
        var height = 0
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        when (heightMode) {
            View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.AT_MOST -> { // 未指定大小 或 wrap_content
                iFrameHeight = iFrameWidth
                height = iFrameHeight + framePadding.toInt() * 2
            }
            View.MeasureSpec.EXACTLY -> { // match_parent或具体的值
                height = View.MeasureSpec.getSize(heightMeasureSpec)
                iFrameHeight = height - framePadding.toInt() * 2
            }
        }
        L.e("width--->>>$width   height--->>>$height")
        setMeasuredDimension(width, height)
    }

    /**
     * 具体查看 drawable-hdpi/diy_input_view.png
     * 图片中部分值在代码里面的实现：topBottomHeight = framePadding
     *                               r = frameTxtHiddenWidth
     */
    override fun onDraw(canvas: Canvas?) {
        // 绘制矩形
        framePaint.color = frameColor
        for (i in 0 until frameLength) {
            val rectF = RectF(
                    framePadding * (i + 1) + iFrameWidth * i,
                    framePadding,
                    framePadding * (i + 1) + iFrameWidth * (i + 1),
                    framePadding + iFrameHeight
            )
            canvas?.drawRoundRect(rectF, frameRadius, frameRadius, framePaint)
        }
        drawInput(canvas)
    }

    // 绘制输入的东西
    private fun drawInput(canvas: Canvas?) {
        framePaint.color = frameColor2
        for (i in 0 until textIndex) {
            val rectF = RectF(
                    framePadding * (i + 1) + iFrameWidth * i,
                    framePadding,
                    framePadding * (i + 1) + iFrameWidth * (i + 1),
                    framePadding + iFrameHeight
            )
            canvas?.drawRoundRect(rectF, frameRadius, frameRadius, framePaint)

            if (frameTxtHidden) {
                drawHiddenSth(canvas, rectF, i)
            } else {
                drawTxt(canvas, rectF, i)
            }

        }
    }

    /**
     * 绘制密文
     */
    private fun drawHiddenSth(canvas: Canvas?, rectF: RectF, index: Int) {
        when (frameTxtHiddenType) {
            FRAME_TXT_HIDDEN_TYPE_CIRCLE -> { // 圆
                canvas?.drawCircle(rectF.centerX(), rectF.centerY(), frameTxtHiddenWidth, pwdPaint)
            }
            FRAME_TXT_HIDDEN_TYPE_RECTANGLE -> { // 矩形
                val oW = (iFrameWidth - (frameTxtHiddenWidth * 2)) / 2
                val oL = (iFrameHeight - (frameTxtHiddenWidth * 2)) / 2
                val rect = RectF(
                        rectF.left + oW,
                        rectF.top + oL,
                        rectF.right - oW,
                        rectF.bottom - oL
                )
                canvas?.drawRoundRect(rect, frameRadius, frameRadius, pwdPaint)
            }
            FRAME_TXT_HIDDEN_TYPE_TRIANGLE -> { // 三角形
                val oW = (iFrameWidth - (frameTxtHiddenWidth * 2)) / 2
                val oL = (iFrameHeight - (frameTxtHiddenWidth * 2)) / 2
                val path = Path()
                // 移动至顶底
                path.moveTo(rectF.left + oW + frameTxtHiddenWidth, rectF.top + oL)
                // 移动至 左下角
                path.lineTo(rectF.left + oW, rectF.top + oL + frameTxtHiddenWidth * 2)
                // 移动至 右下角
                path.lineTo(rectF.left + oW + frameTxtHiddenWidth * 2, rectF.top + oL + frameTxtHiddenWidth * 2)
                path.close()
                canvas?.drawPath(path, pwdPaint)
            }

        }
    }

    // 绘制文字
    private fun drawTxt(canvas: Canvas?, rectF: RectF, index: Int) {
        val txtRect = Rect()
        val value = texts[index]
        // 获取单个text的宽，高
        txtPaint.getTextBounds(value, 0, value.length, txtRect)
        val txtX = rectF.left + iFrameWidth / 2 - txtRect.width() / 2
        val txtY = rectF.top + iFrameHeight / 2 + txtRect.height() / 2
        canvas?.drawText(value, txtX, txtY, txtPaint)
    }

    private fun checkInput(inputValue: String) {
        when (inputValue) {
            KeyBoardUtil.KEY_BOARD_VAL_OK -> {
                inputValueListener?.inputEnd(getText())
            }
            KeyBoardUtil.KEY_BOARD_VAL_DEL -> {
                if (textIndex - 1 >= 0) {
                    textIndex--
                    texts.removeAt(textIndex)
                } else {
                    textIndex = 0
                }
                invalidate()
            }
            KeyBoardUtil.KEY_BOARD_VAL_NOTHING -> L.e("KEY_BOARD_VAL_NOTHING")
            else -> {
                if (textIndex + 1 > frameLength) {
                    textIndex = frameLength
                } else { // 保存
                    textIndex ++
                    texts.add(textIndex - 1, inputValue)
                }
                inputValueListener?.input(inputValue, getText())
                if (isInputDown()) {
                    inputValueListener?.inputEnd(getText())
                }
                invalidate()
            }
        }
    }

    fun clear() {
        texts.clear()
        inputValueListener = null
    }
}