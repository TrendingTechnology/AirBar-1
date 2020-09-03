package com.shahryar.airbar

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi

class AirBar(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val attributes = context.obtainStyledAttributes(attrs, R.styleable.AirBar)

    var levelFillColor: Int = attributes.getResourceId(R.styleable.AirBar_levelFillColor, Color.parseColor("#6274F6"))
    set(value) {
        field = value
        invalidate()
    }

    var backgroundCornerRadius: Float = attributes.getFloat(R.styleable.AirBar_backgroundCornerRadius, 50F)
    set(value) {
        field = value
        invalidate()
    }

    var backgroundSurfaceColor: Int = attributes.getColor(R.styleable.AirBar_backgroundSurfaceColor, Color.parseColor("#E3EAFB"))
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onDraw(canvas: Canvas?) {


        //First init of rect
        if (isVirgin) {
            mPaint.color = levelFillColor
            mPaint.style = Paint.Style.FILL

            mLeft = 0F
            mTop = 200F
            mRight = mLeft + width
            mBottom = height + 0F

            mLevelRect.top = mTop
            mLevelRect.left = mLeft
            mLevelRect.bottom = mBottom
            mLevelRect.right = mRight
        }

        canvas?.drawRect(mLevelRect, mPaint)

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
            mLevelRect.top = event.y
            invalidate()
            return true
        }
        return true
    }


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
}