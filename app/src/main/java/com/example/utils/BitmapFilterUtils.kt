package com.example.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

enum class PhotoFilter(val displayName: String) {
    ORIGINAL("Normal"),
    EMERALD_GLOW("Emerald"),
    SEPIA("Sepia"),
    GRAYSCALE("B&W"),
    VINTAGE("Vintage"),
    HIGH_CONTRAST("Vivid"),
    INVERT("Invert"),
    CYBERPUNK("Cyber")
}

object BitmapFilterUtils {

    fun applyAdjustmentsAndFilter(
        source: Bitmap,
        filter: PhotoFilter,
        brightness: Float, // -100 to +100 (0 default)
        contrast: Float,   // 0.5 to 2.0 (1.0 default)
        saturation: Float  // 0.0 to 2.0 (1.0 default)
    ): Bitmap {
        val result = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Combined Color Matrix
        val combinedMatrix = ColorMatrix()

        // 1. Saturation
        val satMatrix = ColorMatrix().apply { setSaturation(saturation) }
        combinedMatrix.postConcat(satMatrix)

        // 2. Contrast & Brightness
        val scale = contrast
        val translate = brightness + (1f - scale) * 128f / 2f
        val contrastMatrix = ColorMatrix(
            floatArrayOf(
                scale, 0f, 0f, 0f, translate,
                0f, scale, 0f, 0f, translate,
                0f, 0f, scale, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
        )
        combinedMatrix.postConcat(contrastMatrix)

        // 3. Preset Filter
        val filterMatrix = when (filter) {
            PhotoFilter.ORIGINAL -> null
            PhotoFilter.GRAYSCALE -> ColorMatrix().apply { setSaturation(0f) }
            PhotoFilter.SEPIA -> ColorMatrix().apply {
                set(
                    floatArrayOf(
                        0.393f, 0.769f, 0.189f, 0f, 0f,
                        0.349f, 0.686f, 0.168f, 0f, 0f,
                        0.272f, 0.534f, 0.131f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
            }
            PhotoFilter.EMERALD_GLOW -> ColorMatrix(
                floatArrayOf(
                    0.4f, 0f, 0f, 0f, 0f,
                    0.2f, 1.3f, 0.2f, 0f, 30f,
                    0.1f, 0.3f, 0.6f, 0f, 10f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PhotoFilter.VINTAGE -> ColorMatrix(
                floatArrayOf(
                    1.2f, 0.1f, 0.1f, 0f, 10f,
                    0.1f, 1.0f, 0.1f, 0f, 5f,
                    0.1f, 0.1f, 0.8f, 0f, -10f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PhotoFilter.HIGH_CONTRAST -> ColorMatrix(
                floatArrayOf(
                    1.3f, 0f, 0f, 0f, -20f,
                    0f, 1.3f, 0f, 0f, -20f,
                    0f, 0f, 1.3f, 0f, -20f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PhotoFilter.INVERT -> ColorMatrix(
                floatArrayOf(
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            PhotoFilter.CYBERPUNK -> ColorMatrix(
                floatArrayOf(
                    0.8f, 0f, 0.5f, 0f, 20f,
                    0f, 1.2f, 0.3f, 0f, 10f,
                    0.4f, 0f, 1.5f, 0f, 30f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
        }

        if (filterMatrix != null) {
            combinedMatrix.postConcat(filterMatrix)
        }

        paint.colorFilter = ColorMatrixColorFilter(combinedMatrix)
        canvas.drawBitmap(source, 0f, 0f, paint)

        return result
    }

    fun rotateBitmap(source: Bitmap, degrees: Float): Bitmap {
        if (degrees == 0f) return source
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun flipBitmap(source: Bitmap, horizontal: Boolean): Bitmap {
        val matrix = Matrix().apply {
            if (horizontal) postScale(-1f, 1f, source.width / 2f, source.height / 2f)
            else postScale(1f, -1f, source.width / 2f, source.height / 2f)
        }
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun saveBitmapToCache(context: Context, bitmap: Bitmap): File {
        val fileName = "PakAI_Photo_${System.currentTimeMillis()}.jpg"
        val file = File(context.cacheDir, fileName)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
        }
        return file
    }

    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String): Uri? {
        val contentValues = android.content.ContentValues().apply {
            put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/PakAI")
        }
        val uri = context.contentResolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            context.contentResolver.openOutputStream(it).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out!!)
            }
        }
        return uri
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        // Resize if too huge to keep payload fast
        val scaled = if (bitmap.width > 1024 || bitmap.height > 1024) {
            val scale = 1024f / maxOf(bitmap.width, bitmap.height)
            Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(), (bitmap.height * scale).toInt(), true)
        } else {
            bitmap
        }
        scaled.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
