package com.zp.rx_java_t.view.diy

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.zp.rx_java_t.R
import com.zp.rx_java_t.content.dip2px
import com.zp.rx_java_t.content.getColorById
import com.zp.rx_java_t.util.system.L
import java.lang.IllegalArgumentException

class BatteryView : View {

    companion object {
        private const val DEFAULT_OUT_RECT_COLOR = R.color.gray
        private const val DEFAULT_OUT_RECT_WIDTH = 3f
        private const val DEFAULT_INNER_RECT_COLOR = R.color.green
        private const val DEFAULT_OUTINNER_PADDING = 3f
        private const val DEFAULT_RADIAN = 2f

        private const val DEFAULT_ANODE_WIDTH = 5f
        private const val DEFAULT_ANODE_HEIGHT = 13f

        const val ANODE_ORIENTATION_LEFT = 0
        const val ANODE_ORIENTATION_RIGHT = 1
        const val ANODE_ORIENTATION_TOP = 2
        const val ANODE_ORIENTATION_BOTTOM = 3
    }

    /** 外框的颜色 */
    var outRectColor = getColorById(DEFAULT_OUT_RECT_COLOR)
        set(value) {
            field = value
            invalidate()
        }
    /** 外框的宽度 */
    var outRectWidth = DEFAULT_OUT_RECT_WIDTH
        set(value) {
            field = value
            invalidate()
        }
    /** 内框的颜色 */
    var innerRectColor = getColorById(DEFAULT_INNER_RECT_COLOR)
        set(value) {
            field = value
            invalidate()
        }
    /** 內外框之间的边距 */
    var outInnerPadding = DEFAULT_OUTINNER_PADDING
        set(value) {
            field = value
            invalidate()
        }
    /** 正极的宽 */
    var anodeWidth = 0f
        set(value) {
            field = value
            invalidate()
        }
    /** 正极的高 */
    var anodeHeigth = 0f
        set(value) {
            field = value
            invalidate()
        }
    /** 正极的方向 */
    var anodeOrientation = ANODE_ORIENTATION_RIGHT
        set(value) {
            field = value
            invalidate()
        }

    /** 弧度 */
    var radian = DEFAULT_RADIAN
        set(value) {
            field = value
            invalidate()
        }

    /** 电池电量 0到100 */
    var batteryValue = 100
        set(value) {
            field = when {
                value <= 0 -> 0
                value > 100 -> 100
                else -> value
            }
            invalidate()
        }

    private var paint: Paint? = null

    private var outRectF: RectF? = null
    private var anodeRectF: RectF? = null
    private var innerRectF: RectF? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val array = context!!.obtainStyledAttributes(attrs, R.styleable.BatteryView)
        outRectColor = array.getColor(R.styleable.BatteryView_bv_outRectColor, getColorById(DEFAULT_OUT_RECT_COLOR))
        outRectWidth = array.getDimension(R.styleable.BatteryView_bv_outRectWidth, DEFAULT_OUT_RECT_WIDTH)
        innerRectColor = array.getColor(R.styleable.BatteryView_bv_innerRectColor, getColorById(DEFAULT_INNER_RECT_COLOR))
        outInnerPadding = array.getDimension(R.styleable.BatteryView_bv_outInnerPadding, DEFAULT_OUTINNER_PADDING)

        anodeWidth = array.getDimension(R.styleable.BatteryView_bv_anodeWidth, DEFAULT_ANODE_WIDTH)
        anodeHeigth = array.getDimension(R.styleable.BatteryView_bv_anodeHeight, DEFAULT_ANODE_HEIGHT)
        anodeOrientation = array.getInteger(R.styleable.BatteryView_bv_anodeOrientation, ANODE_ORIENTATION_RIGHT)

        radian = array.getDimension(R.styleable.BatteryView_bv_radian, DEFAULT_RADIAN)
        batteryValue = array.getInteger(R.styleable.BatteryView_bv_value, 100)
        array.recycle()
        initAll()
    }

    private fun initAll() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG).run {
            this.style = Paint.Style.STROKE
            this.color = outRectColor
            this.strokeWidth = outRectWidth
            this
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = 0
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        when (widthMode) {
            View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.AT_MOST -> {  // 未指定大小 或 wrap_content
                width = dip2px(20f)
            }
            View.MeasureSpec.EXACTLY -> {  // match_parent或具体的值
                width = View.MeasureSpec.getSize(widthMeasureSpec)
            }
        }
        var height = 0
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        when (heightMode) {
            View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.AT_MOST -> {
                height = dip2px(10f)
            }
            View.MeasureSpec.EXACTLY -> {
                height = View.MeasureSpec.getSize(heightMeasureSpec)
            }
        }
        L.e("battery width--->>>$width   battery height--->>>$height")
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        change(width, height)
        paint?.color = outRectColor
        paint?.style = Paint.Style.STROKE
        // 画外框
        canvas?.drawRoundRect(outRectF, radian, radian, paint)
        // 画电池正极
        paint?.style = Paint.Style.FILL
        canvas?.drawRoundRect(anodeRectF, radian, radian, paint)

        paint?.color = innerRectColor
        // 画电池内部
        canvas?.drawRoundRect(innerRectF, radian * 2, radian * 2, paint)
    }

    private fun change(w: Int, h: Int) {
        when (anodeOrientation) {
            ANODE_ORIENTATION_LEFT -> {
                outRectF = RectF(anodeWidth, 0f, w.toFloat(), h.toFloat())

                anodeRectF = RectF(
                        0f,
                        (h / 2f) - (anodeHeigth / 2),
                        anodeWidth,
                        (h / 2f) - (anodeHeigth / 2) + anodeHeigth)

                var innerLeft = anodeRectF!!.width() + outInnerPadding
                val innerRight = outRectF!!.width() + outInnerPadding

                innerLeft += innerRight * (1 - batteryValue / 100f)

                innerRectF = RectF(
                        innerLeft,
                        outInnerPadding,
                        innerRight,
                        outRectF!!.height() - outInnerPadding
                )
            }
            ANODE_ORIENTATION_RIGHT -> {
                outRectF = RectF(0f, 0f, w - anodeWidth, h.toFloat())

                anodeRectF = RectF(
                        outRectF!!.right,
                        (h / 2f) - (anodeHeigth / 2),
                        w.toFloat(),
                        (h / 2f) - (anodeHeigth / 2) + anodeHeigth)

                var innerRight = outRectF!!.width() - outInnerPadding
                innerRight *= (batteryValue / 100f)

                innerRectF = RectF(
                        outInnerPadding,
                        outInnerPadding,
                        innerRight,
                        outRectF!!.height() - outInnerPadding
                )
            }
            ANODE_ORIENTATION_TOP -> {
                outRectF = RectF(0f, anodeHeigth, w.toFloat(), h.toFloat())

                anodeRectF = RectF(
                        outRectF!!.width() / 2 - (anodeWidth / 2),
                        0f,
                        outRectF!!.width() / 2 - (anodeWidth / 2) + anodeWidth,
                        anodeHeigth
                )

                val innerBottom = h - outInnerPadding
                var innerTop = anodeHeigth + outInnerPadding

                innerTop += innerBottom * (1 - batteryValue / 100f)

                innerRectF = RectF(
                        outInnerPadding,
                        innerTop,
                        w - outInnerPadding,
                        innerBottom
                )

            }
            ANODE_ORIENTATION_BOTTOM -> {
                outRectF = RectF(0f, 0f, w.toFloat(), h - anodeHeigth)

                anodeRectF = RectF(
                        outRectF!!.width() / 2 - (anodeWidth / 2),
                        outRectF!!.bottom,
                        outRectF!!.width() / 2 - (anodeWidth / 2) + anodeWidth,
                        outRectF!!.bottom + anodeHeigth
                )

                var innerBottom = outRectF!!.bottom - outInnerPadding
                innerBottom *= (batteryValue / 100f)

                innerRectF = RectF(
                        outInnerPadding,
                        outInnerPadding,
                        w - outInnerPadding,
                        innerBottom
                )

            }
            else -> throw IllegalArgumentException("BatteryView anode-orientation Error")
        }
    }

}