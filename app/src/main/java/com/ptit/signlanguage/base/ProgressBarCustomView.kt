package com.ptit.signlanguage.base

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View

class ProgressBarCustomView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var width : Int? = null
    private var height : Int? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.color = 0xFFd9d9d9.toInt()
    }
    private val rectF = RectF()
    private val paint2 = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rectF2 = RectF()
    private val mHandler = Handler(Looper.getMainLooper())
    private var progressBarValue = 0f
    init {

    }
    private fun setSizeProgressBar(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> Math.min(width?: 0, widthSize)
            else -> width
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> Math.min(height?: 0, heightSize)
            else -> height
        }

        setMeasuredDimension(width ?: 0, height?: 0)
    }
    fun setOverlayTintColor(color: Int? ) {
        paint2.color = color ?: 0
        invalidate()
    }
    fun setProgressBarDuration(value: Float) {
        mHandler.postDelayed( {
            while(progressBarValue < value) {
                progressBarValue += 10
            }
            invalidate()
        },100)
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        rectF.set(50f, 50f, width!! - 50f, height!! - 50f)
        rectF2.set(50f,50f, width!! - magicConvert(progressBarValue), height!! - 50f)
        canvas.drawRoundRect(rectF, 20f, 20f, paint)
    }
    private fun magicConvert(value: Float) = (value * width!! / 100).toFloat()
}