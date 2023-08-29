package com.gh.gh52hz

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.widget.ImageView

/**
 * 圆形/圆角图片
 * 目前只能通过代码定义属性
 */
class RoundImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    companion object {
        //圆角/圆形
        private const val TYPE_REC = 1
        private const val TYPE_CIRCLE = 2

        //金立机型
        private const val GIONEE = "GIONEE"
    }

    //圆形半径
    private var mCircleRadius = 0f

    //圆角半径
    private var mRoundRadius = 0f

    //显示图片
    private var mBitmap: Bitmap? = null

    //图片画笔
    private var mBitmapPaint: Paint = Paint()

    //填充样式
    private var mBitmapShader: Shader? = null

    //绘制区域
    private var mBitmapRectF = RectF()

    //圆角/圆形
    private var mType = TYPE_REC

    override fun setImageDrawable(drawable: Drawable?) {
        mBitmap = getBitmapFromeDrawable(drawable)
        preDraw()
    }

    fun setRoundRadius(radius: Float) {
        mRoundRadius = radius
        preDraw()
    }

    fun setType(type: Int) {
        mType = type
        preDraw()
    }

    private fun preDraw() {
        if (mBitmap == null) return
        mBitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        mBitmapPaint.shader = mBitmapShader
        mBitmapRectF.set(0f, 0f, width.toFloat(), height.toFloat())
        mCircleRadius = (mBitmapRectF.height() / 2).coerceAtMost(mBitmapRectF.width() / 2)
        invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        preDraw()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        preDraw()
    }

    override fun onDraw(canvas: Canvas?) {
        mBitmap?.let {
            when (mType) {
                TYPE_CIRCLE -> {
                    canvas?.drawCircle(
                        (width / 2).toFloat(),
                        (height / 2).toFloat(),
                        mCircleRadius,
                        mBitmapPaint
                    )
                }

                TYPE_REC -> {
                    //不区分大小写的比较
                    if (GIONEE.equals(Build.MANUFACTURER, true)) {
                        mBitmapPaint.shader = null
                        canvas?.drawBitmap(mBitmap!!, null, mBitmapRectF, mBitmapPaint)
                    } else {
                        canvas?.drawRoundRect(
                            mBitmapRectF,
                            mRoundRadius,
                            mRoundRadius,
                            mBitmapPaint
                        )
                    }
                }

                else -> {}
            }
        }
    }

    private fun getBitmapFromeDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) return null

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        try {

            val bitmap = if (drawable is ColorDrawable) {
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            } else {
                Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        } catch (e: OutOfMemoryError) {
            return null
        }
    }

}