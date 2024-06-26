package com.ptit.signlanguage.ui.predict

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.lifecycle.MutableLiveData
import com.google.inject.Module
import com.ptit.signlanguage.base.BaseViewModel
import java.io.File
import java.io.File.separator
import java.io.FileOutputStream
import java.io.OutputStream


class RealtimeDetectVM: BaseViewModel() {
    private var mModule: Module? = null
    private val DELETE = 26
    private val NOTHING = 27
    private val SPACE = 28
    val imageSize = 96
    var resultString: MutableLiveData<AnalysisResult> = MutableLiveData<AnalysisResult>()
    class AnalysisResult(val result: String)
    @OptIn(ExperimentalGetImage::class)
    fun analyzeImage(context: Context, image: ImageProxy) {
        if (mModule == null) {
            try {

            } catch (e: Exception) {
            }
        }
        var bitmap = image.toBitmap()
        var matrix = Matrix()
        matrix.postRotate(90.0f);
        val dimension = bitmap.width.coerceAtMost(bitmap.height);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap,dimension,dimension)
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true);
        val handBitmap = Bitmap.createScaledBitmap(bitmap, imageSize, imageSize, true);
        saveBitmap(context,handBitmap)
//        HandDetection(context, handBitmap)
    }

    private fun saveBitmap(context: Context, handBitmap: Bitmap) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + "SignLanguage")
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(handBitmap, context.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
            }
        } else {
            val directory = File(Environment.getExternalStorageDirectory().toString() + separator + "SignLanguage")
            // getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = System.currentTimeMillis().toString() + ".png"
            val file = File(directory, fileName)
            saveImageToStream(handBitmap, FileOutputStream(file))
            if (file.absolutePath != null) {
                val values = contentValues()
                values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                // .DATA is deprecated in API 29
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }
        }
    }
    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun contentValues() : ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values
    }
//    private fun HandDetection(context: Context, bitmap: Bitmap) {
//        val model = HandModel.newInstance(context)
//
//// Creates inputs for reference.
//        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 300, 300, 3), DataType.FLOAT32)
//        val byteBuffer = ByteBuffer.allocateDirect(4 * 300 * 300 * 3)
//        byteBuffer.order(ByteOrder.nativeOrder())
//        val intValues = IntArray(300 * 300)
//        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight())
//        var pixel = 0
//        //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
//        //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
//        for (i in 0 until 300) {
//            for (j in 0 until 300) {
//                val `val` = intValues[pixel++] // RGB
//                byteBuffer.putFloat((`val` shr 16 and 0xFF) * (1f / 225))
//                byteBuffer.putFloat((`val` shr 8 and 0xFF) * (1f / 255))
//                byteBuffer.putFloat((`val` and 0xFF) * (1f / 255))
//            }
//        }
//        inputFeature0.loadBuffer(byteBuffer)
//
//// Runs model inference and gets result.
//        val outputs = model.process(inputFeature0)
//        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray
//        val outputFeature1 = outputs.outputFeature1AsTensorBuffer.floatArray
//        val outputFeature2 = outputs.outputFeature2AsTensorBuffer.floatArray
//        val outputFeature3 = outputs.outputFeature3AsTensorBuffer.floatArray
//        Log.d("value", outputFeature0.joinToString { " | " })
//        Log.d("value", outputFeature1.joinToString { " | " })
//        Log.d("value", outputFeature2.joinToString { " | " })
//        model.close()
//    }



}