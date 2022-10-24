package com.example.samplechart.SimplePieChart


import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import com.example.sb_stores.R


class SimplePieChart  : View {
    // Attrs
    private var textSize = 0f
    private var iconSize = 0f
    private var decorRingWeight = DEFAULT_DECOR_RING_WEIGHT
    private var innerHoleWeight = DEFAULT_INNER_HOLE_WEIGHT


    @ColorInt
    private var decorRingColor = DEFAULT_DECOR_RING_COLOR

    @ColorInt
    private var textColor = DEFAULT_TEXT_COLOR

    // Helpers
    private var slices: MutableList<Slice>? = null
    private var chartRadius = 0f
    private var contentBounds: RectF? = null
    private var innerHoleBounds: RectF? = null

    // Drawing helpers
    private var slicePaint: Paint? = null
    private var slicePath: Path? = null
    private var ringPaint: Paint? = null
    private var textPaint: Paint? = null

    constructor(context: Context?) : super(context) {
        initDefaultAttrs()
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initAttrs(attrs)
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttrs(attrs)
        init()
    }

    private fun initDefaultAttrs() {
        // These attributes are depending on Display Metrics
        val dm = resources.displayMetrics
        textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE_SP.toFloat(), dm
        ).toInt().toFloat()
        iconSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, DEFAULT_ICON_SIZE_DP.toFloat(), dm
        ).toInt().toFloat()


    }

    private fun initAttrs(attrs: AttributeSet?) {
        val array = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AwesomePieChart,
            0, 0
        )
        try {
            val dm = resources.displayMetrics
            textSize = array.getDimensionPixelSize(
                R.styleable.AwesomePieChart_textSize, TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE_SP.toFloat(), dm
                ).toInt()
            ).toFloat()
            textColor = array.getColor(R.styleable.AwesomePieChart_textColor, R.style.PrimaryText)
            iconSize = array.getDimensionPixelSize(
                R.styleable.AwesomePieChart_iconSize, TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, DEFAULT_ICON_SIZE_DP.toFloat(), dm
                ).toInt()
            ).toFloat()
            decorRingColor = array.getColor(
                R.styleable.AwesomePieChart_decorRingColor, DEFAULT_DECOR_RING_COLOR
            )
            decorRingWeight = array.getFloat(
                R.styleable.AwesomePieChart_decorRingWeight, DEFAULT_DECOR_RING_WEIGHT
            )
            innerHoleWeight = array.getFloat(
                R.styleable.AwesomePieChart_innerHoleWeight, DEFAULT_INNER_HOLE_WEIGHT
            )
            DEFAULT_TEXT_COLOR = array.getColor(R.styleable.AwesomePieChart_decorRingColor, R.style.PrimaryText)

        } finally {
            array.recycle()
        }
    }

    private fun init() {
        slices = ArrayList()
        contentBounds = RectF()
        innerHoleBounds = RectF()
        slicePaint = Paint()
        slicePaint!!.isAntiAlias = true
        slicePaint!!.style = Paint.Style.FILL
        ringPaint = Paint()
        ringPaint!!.isAntiAlias = true
        ringPaint!!.style = Paint.Style.FILL
        ringPaint!!.color = decorRingColor
        ringPaint!!.alpha = Color.alpha(decorRingColor)
        textPaint = Paint()
        textPaint!!.isAntiAlias = true
        textPaint!!.color = textColor
        textPaint!!.alpha = Color.alpha(textColor)
        textPaint!!.textSize = textSize
        slicePath = Path()
    }

    override fun onDraw(canvas: Canvas) {
        drawSlices(canvas)
        drawPercentageValues(canvas)

    }

    private fun drawSlices(canvas: Canvas) {
        var sliceStartAngle = START_ANGLE_OFFSET.toFloat()
        getCenteredSquareBounds((chartRadius * 1.25).toFloat(), contentBounds)
        getCenteredSquareBounds((chartRadius * innerHoleWeight * 2.5).toFloat(), innerHoleBounds)
        for (slice in slices!!) {
            slicePath = com.example.samplechart.SimplePieChart.PathUtils.getSolidArcPath(
                slicePath!!, contentBounds!!, innerHoleBounds!!,
                sliceStartAngle, getSliceAngle(slice)
            )

            slicePaint!!.color = slice.color
            canvas.drawPath(slicePath!!, slicePaint!!)
            sliceStartAngle += getSliceAngle(slice)
        }
    }

    private fun drawPercentageValues(canvas: Canvas) {
        var sliceStartAngle = START_ANGLE_OFFSET.toFloat()
        val textDistance = chartRadius * (0.8f)
        var sliceHalfAngle: Float
        var textCenterX: Float
        var textCenterY: Float
        var sum = 0
        for (slice in slices!!) {
            sum = (sum + slice.value).toInt()
            sliceHalfAngle = sliceStartAngle + getSliceAngle(slice) / 2f
            textCenterX = MathUtils.getPointX(
                contentBounds!!.centerX(), textDistance, sliceHalfAngle
            )
            textCenterY = MathUtils.getPointY(
                contentBounds!!.centerY(), textDistance, sliceHalfAngle
            )
            val text = getSlicePercentagesString(slice)
            val bounds = Rect()

            textPaint!!.getTextBounds(text, 0, text.length, bounds)
            textPaint!!.typeface = Typeface.DEFAULT_BOLD
            textPaint!!.color = textColor
            textPaint!!.textSize = textSize
            textPaint!!.textAlign = Paint.Align.CENTER
            canvas.drawText(
                slice!!.value.toInt().toString(),
                textCenterX +10 ,
                textCenterY -20,
                textPaint!!
            )
            canvas.drawText(
                slice.label,
                textCenterX +10 ,
                textCenterY + (2* DEFAULT_TEXT_SIZE_SP)  -10,
                textPaint!!
            )
            canvas.drawText("Total Spends",contentBounds!!.centerX() ,
                contentBounds!!.centerY() -20,
                textPaint!!)


            sliceStartAngle += getSliceAngle(slice)
        }
        canvas.drawText("₹ $sum",contentBounds!!.centerX() ,
            contentBounds!!.centerY() +20,
            textPaint!!)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Finding radius of largest circle area inside View.
        chartRadius = Math.min(
            w - paddingLeft - paddingRight,
            h - paddingTop - paddingBottom
        ) / 2f
    }

    protected fun getCenteredSquareBounds(squareSize: Float, bounds: RectF?): RectF? {
        bounds!!.left = (paddingLeft
                + (width - paddingLeft - paddingRight - squareSize) / 2)
        bounds.top = (paddingTop
                + (height - paddingTop - paddingBottom - squareSize) / 2)
        bounds.right = (width - paddingRight
                - (width - paddingLeft - paddingRight - squareSize) / 2)
        bounds.bottom = (height - paddingBottom
                - (height - paddingTop - paddingBottom - squareSize) / 2)
        return bounds
    }

    fun addSlice(slice: Slice) {
        slices!!.add(slice)
        invalidate()
    }

    fun removeAllSices() {
        slices!!.clear()
        invalidate()
    }

    fun getSlicePercentagesString(slice: Slice): String {
        return Math.round(slice.value / total * 100).toString() + "%"
    }

    private fun getSliceAngle(slice: Slice): Float {
        return slice.value / total * 360
    }

    val total: Float
        get() {
            var total = 0f
            for (slice in slices!!) {
                total += slice.value
            }
            return total
        }

    class Slice(
        @field:ColorInt @get:ColorInt
        @param:ColorInt val color: Int, val value: Float , val label: String, val drawable: Drawable ? = null
    )

    companion object {
        // Slices start at 12 o'clock
        protected const val START_ANGLE_OFFSET = 270
        private const val DEFAULT_TEXT_SIZE_SP = 10
        private var DEFAULT_TEXT_COLOR = Color.BLUE
        private const val DEFAULT_ICON_SIZE_DP = 48
        private val DEFAULT_DECOR_RING_COLOR = Color.parseColor("#33ffffff")
        private const val DEFAULT_DECOR_RING_WEIGHT = 0.2f
        private const val DEFAULT_INNER_HOLE_WEIGHT = 0.28f
    }
}