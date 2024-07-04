package com.ptit.signlanguage.ui.tensorflowdetect

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.math.roundToInt

object Detection {
    private var lock: Any = Any()
    private const val TAG = "TFLite-VidClassify"
    private const val MODEL_FPS = 8 // Ensure the input images are fed to the model at this fps.
    private const val MODEL_FPS_ERROR_RANGE = 0.1 // Acceptable error range in fps.
    private const val MAX_RESULT = 3
    private var lastInferenceStartTime: Long = 0
    private var videoClassifier: VideoClassifier? = null
    private var numThread = 4
    private var frames: Array<Bitmap> = arrayOf<Bitmap>()

    fun processImage(
        context: Context,
        bitmap: Bitmap,
    ): Prediction {
        // Ensure that only one frame is processed at any given moment.
        var res = Prediction("", 0f)
        synchronized(lock) {
            val currentTime = SystemClock.uptimeMillis()
            val diff = currentTime - lastInferenceStartTime

            // Check to ensure that we only run inference at a frequency required by the
            // model, within an acceptable error range (e.g. 10%). Discard the frames
            // that comes too early.
            if (diff * MODEL_FPS >= 1000 * (1 - MODEL_FPS_ERROR_RANGE)) {
                lastInferenceStartTime = currentTime
                videoClassifier?.let { classifier ->
                    // Convert the captured frame to Bitmap.
                    val imageBitmap =
                        Bitmap.createBitmap(
                            bitmap.width,
                            bitmap.height,
                            Bitmap.Config.ARGB_8888,
                        )
//                        CalculateUtils.yuvToRgb(image, imageBitmap)

                    // Rotate the image to the correct orientation.
                    val rotateMatrix = Matrix()
                    val rotatedBitmap =
                        Bitmap.createBitmap(
                            imageBitmap,
                            0,
                            0,
                            bitmap.width,
                            bitmap.height,
                            rotateMatrix,
                            false,
                        )
//                        saveBitmap(context, bitmap)
                    // Run inference using the TFLite model.
                    val startTimeForReference = SystemClock.uptimeMillis()
                    val results = classifier.classify(bitmap)
                    val endTimeForReference =
                        SystemClock.uptimeMillis() - startTimeForReference
                    val inputFps = 1000f / diff
//                        showResults(results, endTimeForReference, inputFps)

                    if (inputFps < MODEL_FPS * (1 - MODEL_FPS_ERROR_RANGE)) {
                        Log.w(
                            TAG,
                            "Current input FPS ($inputFps) is " +
                                "significantly lower than the TFLite model's " +
                                "expected FPS ($MODEL_FPS). It's likely because " +
                                "model inference takes too long on this device.",
                        )
                    }
//                        res =  results[0].label + results[0].score
                    res = Prediction(results[0].label, results[0].score)
                }
            }
        }
        return res
    }

    private fun contentValues(): ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        return values
    }

    fun reset() {
        synchronized(lock) {
            videoClassifier?.reset()
        }
    }

    fun saveBitmap(
        context: Context,
        handBitmap: Bitmap,
    ) {
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
            val directory = File(Environment.getExternalStorageDirectory().toString() + File.separator + "SignLanguage")
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

    private fun saveImageToStream(
        bitmap: Bitmap,
        outputStream: OutputStream?,
    ) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getListFrames(mmr: MediaMetadataRetriever): Array<Bitmap> {
        var frames = emptyArray<Bitmap>()
        val NUM_FRAMES = 16
        val duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val numberFrames = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT)
        Log.d("StreamVideoClassifier", "Video length: $duration ms")

        val frameStep: Int

        if (numberFrames != null) {
            frameStep = (numberFrames.toDouble() / NUM_FRAMES).roundToInt()
            for (i in 0 until NUM_FRAMES) {
                var frame = i * frameStep
                if (frame >= numberFrames.toInt()) {
                    frame = numberFrames.toInt() - 1
                }
                val bitmap = mmr.getFrameAtIndex(frame)
                if (bitmap != null) {
                    frames += bitmap
                } else {
                    Log.d("No bitmap", "Found no bitmap at frame: $frame")
                }
            }
        }
        Log.d("StreamVideoClassifier", "Found ${frames.size} frames")
        return frames
    }

    fun createClassifier(context: Context) {
        synchronized(lock) {
            if (videoClassifier != null) {
                videoClassifier?.close()
                videoClassifier = null
            }
            val options =
                VideoClassifier.VideoClassifierOptions
                    .builder()
                    .setMaxResult(MAX_RESULT)
                    .setNumThreads(numThread)
                    .build()
            val modelFile = "model_3.tflite"

            videoClassifier =
                VideoClassifier.createFromFileAndLabelsAndOptions(
                    context,
                    modelFile,
                    "labels.txt",
                    options,
                )

            Log.d(TAG, "Classifier created.")
        }
    }
}
