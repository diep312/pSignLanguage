package com.ptit.signlanguage.ui.tensorflowdetect


import android.graphics.*
import android.media.Image
import java.io.ByteArrayOutputStream
import kotlin.math.exp

object CalculateUtils {

    /**
     * Softmax function
     */
    fun softmax(floatArray: FloatArray): FloatArray {
        var total = 0f
        val result = FloatArray(floatArray.size)
        for (i in floatArray.indices) {
            result[i] = exp(floatArray[i])
            total += result[i]
        }

        for (i in result.indices) {
            result[i] /= total
        }
        return result
    }

    /**
     * Convert ImageProxy to Bitmap
     * Input format YUV420
     */
    fun yuvToRgb(image: Image, output: Bitmap) {
        val yBuffer = image.planes[0].buffer // Y
        val uBuffer = image.planes[1].buffer // U
        val vBuffer = image.planes[2].buffer // V

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
        val imageBytes = out.toByteArray()
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        val canvas = Canvas(output)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
    }
}
