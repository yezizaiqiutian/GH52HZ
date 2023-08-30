package com.gh.gh52hz.img

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.gh.gh52hz.R

class RoundImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {

    private val cornerRadius: Float
    private val path = Path()
    private val rect = RectF()

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView)
        cornerRadius = typedArray.getDimension(R.styleable.RoundImageView_corner_radius, 0f)
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        rect.set(0f, 0f, width.toFloat(), height.toFloat())
        path.reset()
        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)
        canvas.clipPath(path)
        super.onDraw(canvas)
    }
}
