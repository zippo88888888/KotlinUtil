package com.zp.rx_java_t.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.support.annotation.Keep
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.TextView
import com.zp.rx_java_t.R
import com.zp.rx_java_t.content.*
import java.lang.ref.SoftReference

class DiyViewOne1 : View {

    companion object {
        // 默认宽高100dp
        private var DEFAULT_WH = dip2px(100f)
        // 默认宽度
        private var DEFAULT_WIDTH = dip2pxF(10f)
        // 默认最大值
        private const val DEFAULT_MAX_VALUE = 100
        // 默认动画执行的时间
        private const val DEFAULT_DURATION = 600

        /** 左到右渐变 如果需要右到左，把oColor和oColor2值调换即可 */
        private const val GRADIENT_LEFT_TO_RIGHT = 0x100
        /** 上到下渐变 */
        private const val GRADIENT_TOP_TO_BOTTOM = 0x200
    }

    // 宽度
    var oWidth = 0f
    // 背景色
    var oBgColor = 0
    // 填充色
    var oColor = 0
    // 是否渐变
    var oGradient = false
    // 渐变方式
    var oGradientType = 0
    // 填充色2
    var oColor2 = 0
    // 最大值
    var oMaxValue = 0
    // 目前取值
    var oValue = 0
    // 动画执行的时间
    var oDuration = 0

    private lateinit var bgPaint: Paint
    private lateinit var paint: Paint
    private lateinit var rectF: RectF

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val array = context!!.obtainStyledAttributes(attrs, R.styleable.DiyViewOne1)
        oWidth = array.getDimension(R.styleable.DiyViewOne1_oWidth, DEFAULT_WIDTH)
        oBgColor = array.getColor(R.styleable.DiyViewOne1_oBgColor, getColorById(R.color.eeeeee))
        oColor = array.getColor(R.styleable.DiyViewOne1_oColor, getColorById(R.color.baseColor))
        oGradient = array.getBoolean(R.styleable.DiyViewOne1_oGradient, false)
        oGradientType = array.getInteger(R.styleable.DiyViewOne1_oGradientType, GRADIENT_LEFT_TO_RIGHT)
        oColor2 = array.getColor(R.styleable.DiyViewOne1_oColor2, getColorById(R.color.green))
        oMaxValue = array.getInteger(R.styleable.DiyViewOne1_oMaxValue, DEFAULT_MAX_VALUE)
        oValue = array.getInteger(R.styleable.DiyViewOne1_oValue, DEFAULT_MAX_VALUE)
        oDuration = array.getInteger(R.styleable.DiyViewOne1_oDuration, DEFAULT_DURATION)
        array.recycle()
        init()
    }

    private fun init() {
        if (oValue > oMaxValue) oValue = oMaxValue
        rectF = RectF()
        bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).run {
            color = oBgColor
            strokeWidth = oWidth
            style = Paint.Style.STROKE // 画线模式
            this
        }
        paint = Paint(Paint.ANTI_ALIAS_FLAG).run {
            this.strokeWidth = oWidth
            this.style = Paint.Style.STROKE // 画线模式
            this.strokeCap = Paint.Cap.ROUND
            this
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = getSize(DEFAULT_WH, widthMeasureSpec)
        var height = getSize(DEFAULT_WH, heightMeasureSpec)
        if (width < height) {
            height = width
        } else {
            width = height
        }
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        rectF.apply {
            left = oWidth / 2
            right = width.toFloat() - left
            top = oWidth / 2
            bottom = height.toFloat() - top
        }
        if (oGradient) {
            paint.shader = if (oGradientType == GRADIENT_TOP_TO_BOTTOM) {
                LinearGradient(
                    width / 2f,
                    0f,
                    width / 2f,
                    height.toFloat(),
                    oColor,
                    oColor2,
                    Shader.TileMode.CLAMP
                )
            } else {
                LinearGradient(
                    0f,
                    height / 2f,
                    width.toFloat(),
                    height / 2f,
                    oColor,
                    oColor2,
                    Shader.TileMode.CLAMP
                )
            }
        } else {
            paint.color = oColor
        }
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawArc(rectF, -90f, 360f, false, bgPaint)
        canvas?.drawArc(rectF, -90f, changeValue, false, paint)
    }

    @Keep
    private fun setChangeValue(changeValue: Float) {
        this.changeValue = changeValue
        softReference?.get()?.let {
            val endValue = oValue.toFloat() / oMaxValue.toFloat() * 360f
            if (changeValue == endValue) {
                it.text = downValue
                animStart = false
            } else {
                it.text = viewValue + "\n" + doubleFro2((changeValue / 360.0) * 100, "0.0") + "%"
            }
        }
        invalidate()
    }

    private var objAnim: ObjectAnimator? = null
    private var changeValue = 0f
    private var viewValue = ""
    private var downValue = ""
    private var softReference: SoftReference<TextView>? = null
    private var animStart = false

    // 绑定TextView
    fun bindView(view: TextView, value: String = "下载进度", downValue: String = "下载完成") {
        if (softReference == null) {
            softReference = SoftReference(view)
            this.viewValue = value
            this.downValue = downValue
        }
    }

    fun startAnim() {
        if (animStart) return
        val endValue = oValue.toFloat() / oMaxValue.toFloat() * 360f
        objAnim = ObjectAnimator.ofFloat(this, "changeValue", 0f, endValue).run {
            interpolator = AccelerateInterpolator()
            duration = oDuration.toLong()
            start()
            this
        }
        animStart = true
    }

    fun clear() {
        objAnim?.cancel()
        objAnim?.removeAllUpdateListeners()
        objAnim?.removeAllListeners()
        softReference?.clear()
        animStart = false
        objAnim = null
    }

}