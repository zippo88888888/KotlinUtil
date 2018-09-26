package com.zp.rx_java_t.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.support.annotation.Keep
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import com.zp.rx_java_t.R
import com.zp.rx_java_t.content.dip2px
import com.zp.rx_java_t.content.getColorById
import com.zp.rx_java_t.content.getDisplay

/**
 * com.zp.rx_java_t.view
 * Created by ZP on 2018/9/26.
 * description:
 * version: 1.0
 */
class AnimButton : View {

    companion object {
        /** 默认的 R角度数 */
        private const val DEFAULT_ANIM_BUTTON_R = 10
        /** 默认的 背景颜色 */
        private const val DEFAULT_ANIM_BUTTON_BG_COLOR = R.color.baseColor
        /** 默认的 动画持续时间 */
        private const val DEFAULT_ANIM_BUTTON_DURATION = 500

        /** 默认的 文字大小 */
        private const val DEFAULT_ANIM_BUTTON_TXT_SIZE = 40f
        /** 默认的 文字颜色 */
        private const val DEFAULT_ANIM_BUTTON_TXT_COLOR = R.color.white

        /** 默认的 圆弧宽度 */
        private const val DEFAULT_ANIM_BUTTON_ARC_WIDTH = 5f
        /** 默认的 圆弧颜色 */
        private const val DEFAULT_ANIM_BUTTON_ARC_COLOR = R.color.white

        private const val DEFAULT_HEIGHT = 60f

        /** 开始动画 */
        private const val ANIM_START = 1
        /** 结束动画 */
        private const val ANIM_END = 2
    }

    /** R角度数 */
    var animButtonR = DEFAULT_ANIM_BUTTON_R
    /** 背景颜色 */
    var animButtonBgColor = DEFAULT_ANIM_BUTTON_BG_COLOR
    /** 动画持续时间 */
    var animButtonDuration = DEFAULT_ANIM_BUTTON_DURATION
    /** 文字 */
    var animButtonTxt = ""
    /** 文字大小 */
    var animButtonTxtSize = DEFAULT_ANIM_BUTTON_TXT_SIZE
    /** 文字颜色 */
    var animButtonTxtColor = DEFAULT_ANIM_BUTTON_TXT_COLOR
    /** 圆弧宽度 */
    var animButtonArcWidth = DEFAULT_ANIM_BUTTON_ARC_WIDTH
    /** 圆弧颜色 */
    var animButtonArcColor = DEFAULT_ANIM_BUTTON_ARC_COLOR

    private var rW = 0
    private var rH = 0

    private var centerX = 0
    private var centerY = 0

    private var paint: Paint? = null
    private var txtPaint: Paint? = null
    private var arcPaint: Paint? = null

    private lateinit var leftOvalRect: RectF
    private lateinit var rightOvalRect: RectF

    private var oneAnim: ObjectAnimator? = null
    private var leftValue = 0f
    private var rightValue = 0f

    private var animState = ANIM_END

    /** 控件是否是展开状态 */
    private var isShow = true

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val array = context?.obtainStyledAttributes(attrs, R.styleable.AnimButton) ?: return
        animButtonR = array.getInt(R.styleable.AnimButton_animButtonR, DEFAULT_ANIM_BUTTON_R)
        animButtonBgColor = array.getColor(R.styleable.AnimButton_animButtonBgColor, getColorById(DEFAULT_ANIM_BUTTON_BG_COLOR))
        animButtonDuration = array.getInt(R.styleable.AnimButton_animButtonDuration, DEFAULT_ANIM_BUTTON_DURATION)
        animButtonTxtSize = array.getDimension(R.styleable.AnimButton_animButtonTxtSize, DEFAULT_ANIM_BUTTON_TXT_SIZE)
        animButtonTxtColor = array.getColor(R.styleable.AnimButton_animButtonTxtColor, getColorById(DEFAULT_ANIM_BUTTON_TXT_COLOR))
        animButtonArcWidth = array.getDimension(R.styleable.AnimButton_animButtonArcWidth, DEFAULT_ANIM_BUTTON_ARC_WIDTH)
        animButtonArcColor = array.getColor(R.styleable.AnimButton_animButtonArcColor, getColorById(DEFAULT_ANIM_BUTTON_ARC_COLOR))
        animButtonTxt = try {
            array.getString(R.styleable.AnimButton_animButtonTxt)
        } catch (e: Exception) {
            ""
        }

        array.recycle()
        initPaint()
    }

    private fun initPaint() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint?.color = animButtonBgColor

        txtPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        txtPaint?.color = animButtonTxtColor
        txtPaint?.textSize = animButtonTxtSize
        txtPaint?.textAlign = Paint.Align.CENTER

        arcPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        arcPaint?.color = animButtonArcColor
        arcPaint?.strokeWidth = animButtonArcWidth
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpec = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)

        val heightSpec = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        // 计算宽
        val w = if (widthSpec == View.MeasureSpec.EXACTLY) widthSize
        else context.getDisplay()[0]

        // 计算高
        val h = if (heightSpec == View.MeasureSpec.EXACTLY) heightSize
        else dip2px(DEFAULT_HEIGHT)

        rW = w - paddingLeft - paddingRight
        rH = h - paddingTop - paddingBottom

        // 计算中心点
        centerX = rW / 2
        centerY = rH / 2
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        drawOval(canvas)
        drawRect(canvas)
    }

    // 绘制左右两侧圆
    private fun drawOval(canvas: Canvas?) {
        // 左
        leftOvalRect = RectF(
                paddingLeft + leftValue,
                paddingTop.toFloat(),
                paddingLeft + rH + leftValue,
                paddingTop + rH.toFloat()
        )
        // 右
        rightOvalRect = if (rightValue == 0f) RectF(
                width - rH - paddingRight.toFloat(),
                paddingTop.toFloat(),
                width - paddingRight.toFloat(),
                paddingTop + rH.toFloat())
        else RectF(rightValue, // 右边动画一旦有值，就代表为left 的值
                paddingTop.toFloat(),
                rH + rightValue,
                paddingTop + rH.toFloat())
        canvas?.drawOval(leftOvalRect, paint)
        canvas?.drawOval(rightOvalRect, paint)
    }

    // 绘制中间矩形
    private fun drawRect(canvas: Canvas?) {
        val rect = RectF(
                leftOvalRect.left + rH / 2f,
                leftOvalRect.top,
                rightOvalRect.left + rH / 2,
                rightOvalRect.bottom
        )
        canvas?.drawRect(rect, paint)

        if (animButtonTxt.isNotEmpty()) {
            // 文字所在的矩形Rect
            val txtRect = Rect(0, 0, width, height)
            val fontMetrics = txtPaint!!.fontMetricsInt
            // 文字基线
            val txtLine = (txtRect.bottom + txtRect.top - fontMetrics.bottom - fontMetrics.top) / 2f
            canvas?.drawText(animButtonTxt, txtRect.centerX().toFloat(), txtLine, txtPaint)
        }
    }

    private fun startAnim() {
        if (oneAnim == null && animState == ANIM_END) {
            val leftEndValue = centerX - rH / 2f
            val rightEndValue = centerX.toFloat() - paddingRight

            val leftValuesHolder: PropertyValuesHolder
            val rightValuesHolder: PropertyValuesHolder

            if (isShow) { // 展开 --->>> 收缩
                leftValuesHolder = PropertyValuesHolder.ofFloat("leftChange",
                        0f, leftEndValue)
                rightValuesHolder = PropertyValuesHolder.ofFloat("rightChange",
                        width - rH - paddingRight.toFloat(), rightEndValue)
            } else { // 收缩 --->>> 展开
                leftValuesHolder = PropertyValuesHolder.ofFloat("leftChange",
                        leftEndValue, 0f)
                rightValuesHolder = PropertyValuesHolder.ofFloat("rightChange",
                        rightEndValue, width - rH - paddingRight.toFloat())
            }
            isShow = !isShow
            oneAnim = ObjectAnimator.ofPropertyValuesHolder(this, leftValuesHolder, rightValuesHolder)
            oneAnim?.duration = animButtonDuration.toLong()
            oneAnim?.interpolator = LinearInterpolator()
            oneAnim?.addListener(object : AnimatorListenerAdapter() {

                override fun onAnimationStart(animation: Animator?) {
                    animState = ANIM_START
                }

                override fun onAnimationEnd(animation: Animator?) {
                    animState = ANIM_END
                    oneAnim?.cancel()
                    oneAnim = null
                }
            })
            oneAnim?.start()
        }
    }

    @Keep
    private fun setLeftChange(leftValue: Float) {
        this.leftValue = leftValue
        invalidate()
    }

    @Keep
    private fun setRightChange(rightValue: Float) {
        this.rightValue = rightValue
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            startAnim()
            return false
        }
        return super.onTouchEvent(event)
    }

    fun reset() {
        invalidate()
    }
}