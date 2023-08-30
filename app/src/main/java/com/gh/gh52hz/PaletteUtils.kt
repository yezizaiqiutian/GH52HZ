package com.gh.gh52hz

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import java.util.Collections

@Deprecated("直接使用Palette就好")
object PaletteUtils {

    public interface Listener{
        fun onResult(backgroundColor:Int)
    }

    /**
     * 根据图片获取主色值
     */
    fun extract(bitmap: Bitmap?, scale: Float): Int {
        if (bitmap == null) return 0
        val matrix = Matrix()
        matrix.postScale(scale, scale)
        var bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)
        val width = bitmap.width
        val height = bitmap.height

        val topBorderColors = getColorsInRange(bitmap, 0, 0, width - 1, 1)
        val rightBorderColors = getColorsInRange(bitmap, width - 1, 0, 1, height - 1)
        val leftBorderColors = getColorsInRange(bitmap, 0, 1, 1, height - 1)
        val bottomBorderColors = getColorsInRange(bitmap, 1, height - 1, width - 1, 1)
        val borderColors = mutableListOf<Int>()
        borderColors.addAll(topBorderColors)
        borderColors.addAll(rightBorderColors)
        borderColors.addAll(leftBorderColors)
        borderColors.addAll(bottomBorderColors)
        val backgrountColor = getDominantColors(borderColors)[0]
        return backgrountColor
    }

    private fun getColorsInRange(
        bitmap: Bitmap?,
        x: Int,
        y: Int,
        width: Int,
        height: Int
    ): List<Int> {
        val list = mutableListOf<Int>()
//        for (int i = x;i<x+width;i++)
        for (i in x until x + width) {
            for (j in y until y + height) {
                bitmap?.getPixel(i, j)?.let {
                    list.add(it)
                }
            }
        }
        return list
    }

    private fun getDominantColors(list: List<Int>): List<Int> {
        val buckets = getSimilarColors(list, 25.5F)
        Collections.sort(buckets, object : Comparator<List<Int>> {
            override fun compare(bucket1: List<Int>, bucket2: List<Int>): Int {
                return bucket2.size - bucket1.size
            }
        })
        val dominant = mutableListOf<Int>()
        buckets.forEach {
            dominant.add(getMeanColor(it))
        }
        return dominant
    }

    private fun getMeanColor(list: List<Int>): Int {
        val mean = FloatArray(3)
        list.forEach {
            val rgb = getColorRgb(it)
            mean[0] += rgb[0]
            mean[1] += rgb[1]
            mean[2] += rgb[2]
        }

        mean[0] = mean[0] / list.size
        mean[1] = mean[1] / list.size
        mean[2] = mean[2] / list.size
        return Color.rgb(mean[0].toInt(), mean[1].toInt(), mean[2].toInt())
    }

    private fun getColorRgb(color: Int): FloatArray {
        val rgb = FloatArray(3)
        rgb[0] = Color.red(color).toFloat()
        rgb[1] = Color.green(color).toFloat()
        rgb[2] = Color.blue(color).toFloat()
        return rgb
    }

    private fun getSimilarColors(list: List<Int>, compareDistance: Float): List<List<Int>> {
        val subsets = mutableListOf<MutableList<Int>>()
        list.forEach { color ->
            var closest = mutableListOf<Int>()
            var index = 0
            for (index in 0 until subsets.size) {
//            subsets.forEach {
                if (getColorDistance(subsets.get(index).get(0), color) < compareDistance) {
                    return@forEach
                }
            }
            if (index >= subsets.size) {
                closest = mutableListOf()
                subsets.add(closest)
            } else {
                closest = subsets.get(index)
            }
            closest.add(color)
        }
        return subsets
    }

    private fun getColorDistance(color1: Int, color2: Int): Float {
        val yue1 = rgb2Yuv(getColorRgb(color1))
        val yue2 = rgb2Yuv(getColorRgb(color2))
        return (Math.sqrt(Math.pow(yue1[0].toDouble() - yue2[0].toDouble(), 2.0))
                + Math.sqrt(Math.pow(yue1[1].toDouble() - yue2[1].toDouble(), 2.0))
                + Math.sqrt(Math.pow(yue1[2].toDouble() - yue2[2].toDouble(), 2.0))).toFloat()
    }

    private fun rgb2Yuv(rgb: FloatArray): FloatArray {
        val yuv = FloatArray(3)
        yuv[0] = rgb[0]*0.299f+rgb[1]*0.587f+rgb[2]*0.114f
        yuv[0] = rgb[0]*-0.169f+rgb[1]*-0.331f+rgb[2]*0.500f+128.0f
        yuv[0] = rgb[0]*0.500f+rgb[1]*0.419f+rgb[2]*0.081f+128.0f
        return yuv
    }

}