package com.shahryar.airbar

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.log

class AirBar(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val attributes = context.obtainStyledAttributes(attrs, R.styleable.AirBar)
    var listener: OnLevelChangedListener? = null

    var levelFillColor: Int = attributes.getResourceId(
        R.styleable.AirBar_levelFillColor,
        resources.getColor(R.color.defaultLevel)
    )
        set(value) {
            field = value
            levelGradientColor0 = value
            levelGradientColor1 = value
            invalidate()
        }

    var backgroundCornerRadius: Float = attributes.getFloat(R.styleable.AirBar_backgroundCornerRadius, 50F)
    set(value) {
        field = value
        invalidate()
    }

    var backgroundSurfaceColor: Int = attributes.getColor(
        R.styleable.AirBar_backgroundSurfaceColor,
        resources.getColor(R.color.defaultBackground)
    )
        set(value) {
            field = value
            invalidate()
        }

    var icon: Drawable? = attributes.getDrawable(R.styleable.AirBar_icon)
        set(value) {
            field = value
            invalidate()
        }

    var levelGradientColor0: Int =
        attributes.getResourceId(R.styleable.AirBar_levelGradientColor0, levelFillColor)
        set(value) {
            field = value
            invalidate()
        }

    var levelGradientColor1: Int =
        attributes.getResourceId(R.styleable.AirBar_levelGradientColor1, levelFillColor)
        set(value) {
            field = value
            invalidate()
        }

    override fun setBackgroundColor(color: Int) {
        super.setBackgroundColor(color)
        invalidate()
    }

    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    var isVirgin = true

    private var mLeft = 0F
    private var mTop = 200F
    private var mRight = mLeft
    private var mBottom = 0F

    private val mLevelRect = RectF()

    @SuppressLint("DrawAllocation")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onDraw(canvas: Canvas?) {

        mPaint.color = levelFillColor
        mPaint.style = Paint.Style.FILL
        mPaint.shader =
            LinearGradient(
                0F,
                0F,
                0F,
                height.toFloat(),
                levelGradientColor0,
                levelGradientColor1,
                Shader.TileMode.MIRROR
            )


        //First init of rect
        if (isVirgin) {

            mLeft = 0F
            mTop = 200F
            mRight = mLeft + width
            mBottom = height + 0F

            mLevelRect.top = mTop
            mLevelRect.left = mLeft
            mLevelRect.bottom = mBottom
            mLevelRect.right = mRight

        }
        Log.d("tag", "OnDraw : $levelFillColor")

        canvas?.drawRect(mLevelRect, mPaint)

    }

    override fun onDrawForeground(canvas: Canvas?) {
        val bitmap = icon?.toBitmap()
        if (bitmap != null) {
            canvas?.drawBitmap(bitmap, (mRight / 2) - 40F, mBottom - 130F, mPaint)
        }
    }

    override fun draw(canvas: Canvas?) {
        setBackgroundColor(backgroundSurfaceColor)
        //Set rounded corner frame
        canvas?.clipPath(getRoundedRect(0F, 0F, mRight, mBottom, backgroundCornerRadius, backgroundCornerRadius, true, true, true, true)!!)
        super.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_MOVE) {
            isVirgin = false
            if (event.y in 0.0..mBottom.toDouble()) mLevelRect.top = event.y
            else if (event.y > 100) mLevelRect.top = mBottom
            else if (event.y < 0) mLevelRect.top = 0F
            listener?.onLevelChanged(calculateLevel(mLevelRect.top, mBottom))
            invalidate()
            return true
        }
        return true
    }

    private fun calculateLevel(level: Float, capacity: Float): Int {

        return 100 - ((level.toDouble() / capacity.toDouble()) * 100).toInt()
    }


    /**
     * @author Moh Mah at https://stackoverflow.com/a/35668889/10315711
     */
    private fun getRoundedRect(
        left: Float, top: Float, right: Float, bottom: Float, rx: Float, ry: Float,
        tl: Boolean, tr: Boolean, br: Boolean, bl: Boolean
    ): Path? {
        var rx = rx
        var ry = ry
        val path = Path()
        if (rx < 0) rx = 0f
        if (ry < 0) ry = 0f
        val width = right - left
        val height = bottom - top
        if (rx > width / 2) rx = width / 2
        if (ry > height / 2) ry = height / 2
        val widthMinusCorners = width - 2 * rx
        val heightMinusCorners = height - 2 * ry
        path.moveTo(right, top + ry)
        if (tr) path.rQuadTo(0f, -ry, -rx, -ry) //top-right corner
        else {
            path.rLineTo(0f, -ry)
            path.rLineTo(-rx, 0f)
        }
        path.rLineTo(-widthMinusCorners, 0f)
        if (tl) path.rQuadTo(-rx, 0f, -rx, ry) //top-left corner
        else {
            path.rLineTo(-rx, 0f)
            path.rLineTo(0f, ry)
        }
        path.rLineTo(0f, heightMinusCorners)
        if (bl) path.rQuadTo(0f, ry, rx, ry) //bottom-left corner
        else {
            path.rLineTo(0f, ry)
            path.rLineTo(rx, 0f)
        }
        path.rLineTo(widthMinusCorners, 0f)
        if (br) path.rQuadTo(rx, 0f, rx, -ry) //bottom-right corner
        else {
            path.rLineTo(rx, 0f)
            path.rLineTo(0f, -ry)
        }
        path.rLineTo(0f, -heightMinusCorners)

        path.close() //Given close, last lineto can be removed.
        return path
    }

    interface OnLevelChangedListener {
        fun onLevelChanged(level: Int)
    }
}