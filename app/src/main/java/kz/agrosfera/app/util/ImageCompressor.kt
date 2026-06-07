package kz.agrosfera.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

object ImageCompressor {

    private const val MAX_SIDE_PX = 1280
    private const val JPEG_QUALITY = 85

    fun compressForUpload(context: Context, uri: Uri): Pair<ByteArray, String> {
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, opts)
        }
        val sampleSize = calculateSampleSize(opts.outWidth, opts.outHeight, MAX_SIDE_PX)
        val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val bitmap = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, decodeOpts)
        } ?: error("cannot_read_image")

        val scaled = scaleDown(bitmap, MAX_SIDE_PX)
        if (scaled !== bitmap) {
            bitmap.recycle()
        }

        val bytes = ByteArrayOutputStream().use { stream ->
            scaled.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream)
            stream.toByteArray()
        }
        scaled.recycle()
        return bytes to "leaf.jpg"
    }

    private fun calculateSampleSize(width: Int, height: Int, maxSide: Int): Int {
        var size = 1
        while (width / size > maxSide * 2 || height / size > maxSide * 2) {
            size *= 2
        }
        return size
    }

    private fun scaleDown(source: Bitmap, maxSide: Int): Bitmap {
        val w = source.width
        val h = source.height
        val largest = maxOf(w, h)
        if (largest <= maxSide) return source
        val scale = maxSide.toFloat() / largest
        val nw = (w * scale).toInt().coerceAtLeast(1)
        val nh = (h * scale).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(source, nw, nh, true)
    }
}
