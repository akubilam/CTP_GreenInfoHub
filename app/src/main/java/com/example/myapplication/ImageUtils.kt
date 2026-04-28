package com.example.myapplication

import android.graphics.*
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream

object ImageUtils {
    fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val planeProxy = imageProxy.planes[0]
        val buffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
    }

    fun getContrastFilter(): ColorMatrixColorFilter {
        val contrast = 2.0f
        val brightness = -40f
        val cm = ColorMatrix(floatArrayOf(
            contrast, 0f, 0f, 0f, brightness,
            0f, contrast, 0f, 0f, brightness,
            0f, 0f, contrast, 0f, brightness,
            0f, 0f, 0f, 1f, 0f
        ))
        return ColorMatrixColorFilter(cm)
    }
}
