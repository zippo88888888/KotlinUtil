package com.zp.rx_java_t.view.diy

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.inputmethodservice.KeyboardView
import android.util.AttributeSet
import android.view.View
import com.zp.rx_java_t.R
import com.zp.rx_java_t.content.getColorById
import com.zp.rx_java_t.content.getDisplay
import com.zp.rx_java_t.content.getSize
import com.zp.rx_java_t.content.showToast
import com.zp.rx_java_t.util.L

class DiyInputView : View {

    companion object {

        /** 圆 */
        const val FRAME_TXT_HIDDEN_TYPE_CIRCLE = 0x100
        /** 矩形 */
        const val FRAME_TXT_HIDDEN_TYPE_RECTANGLE = 0x200
        /** 三角形 */
        const val FRAME_TXT_HIDDEN_TYPE_TRIANGLE = 0x300

        /** 默认 控件的高度 */
        private const val DEFAULT_FRAME_HEIGHT = 120

        /** 默认 框 未输入的颜色 */
        private const val DEFAULT_FRAME_COLOR = R.color.black
        /** 默认 框 输入后的颜色 */
        private const val DEFAULT_FRAME_COLOR2 = R.color.green
        /** 默认 框 宽度 */
        private const val DEFAULT_FRAME_WIDTH = 5f
        /** 默认 框 圆角大小 */
        private const val DEFAULT_FRAME_RADIUS = 5f
        /** 默认 框 长度 */
        private const val DEFAULT_FRAME_LENGTH = 6
        /** 默认 框 间距 */
        private const val DEFAULT_FRAME_PADDING = 20f

        /** 默认 文字 未输入的颜色 */
        private const val DEFAULT_FRAME_TXT_COLOR = DEFAULT_FRAME_COLOR
        /** 默认 文字 输入后的颜色 */
        private const val DEFAULT_FRAME_TXT_COLOR2 = DEFAULT_FRAME_COLOR2
        /** 默认 文字 大小 */
        private const val DEFAULT_FRAME_TXT_SIZE = 20f

        /** 默认 文字 隐藏后的宽度 */
        private const val DEFAULT_FRAME_TXT_HIDDEN_WIDTH = 10f
        /** 默认 文字 隐藏的样式 */
        private const val DEFAULT_FRAME_TXT_HIDDEN_TYPE = FRAME_TXT_HIDDEN_TYPE_CIRCLE

    }

    // 每个框的实际宽
    private var iFrameWidth = 0
    // 每个框的实际高
    private var iFrameHeight = 0
    // 每个框距离顶部或底部的padding
    private var iFragmentTopBottomPadding = 0


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

    // 文字 未输入的颜色
    var frameTxtColor = 0
    // 文字 输入后的颜色
    var frameTxtColor2 = 0
    // 文字 大小
    var frameTxtSize = 0f

    // 文字输入后是否隐藏
    var frameTxtHidden = true
    // 文字 隐藏后的宽度
    var frameTxtHiddenWidth = 0f
    // 文字 隐藏的样式
    var frameTxtHiddenType = 0

    private lateinit var framePaint: Paint
    private lateinit var txtPaint: Paint
    private lateinit var pwdPaint: Paint

    private var frameRecgF: RectF? = null

    private var keyBoardUtil: KeyBoardUtil? = null
    private var keyBoardView: KeyboardView? = null

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
        frameTxtColor = array.getColor(R.styleable.DiyInputView_iFrameTxtColor, getColorById(DEFAULT_FRAME_TXT_COLOR))
        frameTxtColor2 = array.getColor(R.styleable.DiyInputView_iFrameTxtColor2, getColorById(DEFAULT_FRAME_TXT_COLOR2))
        frameTxtSize = array.getDimension(R.styleable.DiyInputView_iFrameTxtSize, DEFAULT_FRAME_TXT_SIZE)
        frameTxtHidden = array.getBoolean(R.styleable.DiyInputView_iFrameTxtHidden, true)
        frameTxtHiddenWidth = array.getDimension(R.styleable.DiyInputView_iFrameTxtHiddenWidth, DEFAULT_FRAME_TXT_HIDDEN_WIDTH)
        frameTxtHiddenType = array.getInteger(R.styleable.DiyInputView_iFrameTxtHiddenType, DEFAULT_FRAME_TXT_HIDDEN_TYPE)
        array.recycle()
        init()
    }

    private fun init() {
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
        frameRecgF = RectF()
        keyBoardUtil = KeyBoardUtil(context)
        keyBoardUtil?.keyBoardOutPutListener = { checkInput(it)}
    }

    fun bindBoardView(keyBoardView: KeyboardView) {
        this.keyBoardView = keyBoardView
        keyBoardUtil?.showKeyBoard(keyBoardView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = getSize(context.getDisplay()[0], widthMeasureSpec)
        val height = getSize(DEFAULT_FRAME_HEIGHT, heightMeasureSpec)
        L.e("width--->>>$width   height--->>>$height")
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    private fun checkInput(inputType: String) {
        when (inputType) {
            KeyBoardUtil.KEY_BOARD_VAL_OK -> {
                showToast("ok")
            }
            KeyBoardUtil.KEY_BOARD_VAL_DEL -> {
                showToast("del")
            }
            KeyBoardUtil.KEY_BOARD_VAL_NOTHING -> {
                showToast("nothing")
            }
            else -> {
                showToast(inputType)
            }
        }
    }
}