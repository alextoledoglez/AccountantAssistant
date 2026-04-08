package com.personal.accountantAssistant.extensions

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF

fun Bitmap.rotateAndCrop(degrees: Int, rect: RectF): Bitmap {
    val src = if (degrees == 0) this else {
        val m = Matrix().apply { postRotate(degrees.toFloat()) }
        Bitmap.createBitmap(this, 0, 0, width, height, m, false)
    }
    val x = (rect.left * src.width).toInt().coerceIn(0, src.width - 1)
    val y = (rect.top * src.height).toInt().coerceIn(0, src.height - 1)
    val w = (rect.width() * src.width).toInt().coerceIn(1, src.width - x)
    val h = (rect.height() * src.height).toInt().coerceIn(1, src.height - y)
    return Bitmap.createBitmap(src, x, y, w, h)
}