/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.icerockdev.databinding.ActivityMainBinding
import com.icerockdev.library.ResHolder
import com.icerockdev.library.TFDigitClassifier
import dev.icerock.moko.sample.tensorflowtest.MR
import dev.icerock.moko.tensorflow.Interpreter
import dev.icerock.moko.tensorflow.InterpreterOptions
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicBoolean

@Suppress("MagicNumber")
class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    private val interpreter: Interpreter by lazy {
        Interpreter(
            ResHolder.getDigitsClassifierModel(),
            InterpreterOptions(2, useNNAPI = true),
            this
        ).also { isInterpreterInitialized.set(true) }
    }

    private val digitClassifier: TFDigitClassifier by lazy {
        TFDigitClassifier(interpreter, this.lifecycleScope)
    }

    private val isInterpreterInitialized = AtomicBoolean(false)

    @SuppressLint("ClickableViewAccessibility")
    @Suppress("UnnecessarySafeCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.binding = binding

        binding.drawView.setStrokeWidth(70.0f)
        binding.drawView.setColor(Color.WHITE)
        binding.drawView.setBackgroundColor(Color.BLACK)

        binding.clearButton.setOnClickListener {
            binding.drawView.clearCanvas()
            binding.predictedText.setText(MR.strings.draw_digit.resourceId)
        }

        binding.drawView.setOnTouchListener { _, event ->
            binding.drawView.onTouchEvent(event)

            if (event.action == MotionEvent.ACTION_UP) {
                classifyDrawing()
            }

            true
        }

        digitClassifier.initialize()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        interpreter.close()
    }

    private fun classifyDrawing() {
        if (!isInterpreterInitialized.get()) return

        val rawBitmap = binding?.drawView?.getBitmap() ?: return
        val bitmapToClassify = Bitmap.createScaledBitmap(
            rawBitmap,
            digitClassifier.inputImageWidth,
            digitClassifier.inputImageHeight,
            true
        )

        digitClassifier.classifyAsync(convertBitmapToByteBuffer(bitmapToClassify)) {
            runOnUiThread {
                binding?.predictedText?.text = it
            }
        }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(digitClassifier.modelInputSize)
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(digitClassifier.inputImageWidth * digitClassifier.inputImageHeight)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (pixelValue in pixels) {
            val r = (pixelValue shr 16 and 0xFF)
            val g = (pixelValue shr 8 and 0xFF)
            val b = (pixelValue and 0xFF)

            val normalizedPixelValue = (r + g + b) / 3.0f / 255.0f
            byteBuffer.putFloat(normalizedPixelValue)
        }

        return byteBuffer
    }
}
