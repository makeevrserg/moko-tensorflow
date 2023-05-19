package com.icerockdev

import android.graphics.Bitmap
import com.icerockdev.library.classifier.DigitClassifier
import com.icerockdev.library.classifier.TFDigitClassifier
import java.nio.ByteBuffer
import java.nio.ByteOrder

object BitmapConverter {
    private fun normalizePixel(pixelValue: Int): Float {
        val r = (pixelValue shr 16 and 0xFF)
        val g = (pixelValue shr 8 and 0xFF)
        val b = (pixelValue and 0xFF)

        return (r + g + b) / 3.0f / 255.0f
    }

    fun convertBitmapToArray(bitmap: Bitmap): DigitClassifier.Input {
        val input = DigitClassifier.Input(1)
        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val pixelValue = bitmap.getPixel(x, y)
                val normalizedPixelValue = normalizePixel(pixelValue)
                input.array[0][y][x] = normalizedPixelValue
            }
        }
        return input
    }

    fun convertBitmapToByteBuffer(digitClassifier: TFDigitClassifier, bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(digitClassifier.modelInputSize)
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(digitClassifier.inputImageWidth * digitClassifier.inputImageHeight)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (pixelValue in pixels) {
            val normalizedPixelValue = normalizePixel(pixelValue)
            byteBuffer.putFloat(normalizedPixelValue)
        }

        return byteBuffer
    }
}