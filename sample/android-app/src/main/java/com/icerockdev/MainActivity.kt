/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package com.icerockdev

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.divyanshu.draw.widget.DrawView
import com.icerockdev.library.classifier.DefaultDigitClassifier
import com.icerockdev.library.classifier.DigitClassifier
import com.icerockdev.library.classifier.TFDigitClassifier
import com.icerockdev.library.ResHolder
import dev.icerock.moko.tensorflow.Interpreter
import dev.icerock.moko.tensorflow.InterpreterOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Suppress("MagicNumber")
class MainActivity : AppCompatActivity() {

    private lateinit var drawView: DrawView
    private lateinit var clearButton: Button
    private lateinit var predictedTextView: TextView
    private lateinit var predictedTFTextView: TextView

    private val interpreter: Interpreter by lazy {
        Interpreter(ResHolder.getDigitsClassifierModel(), InterpreterOptions(2, useNNAPI = true), this)
    }
    private val digitClassifier: DigitClassifier by lazy {
        DefaultDigitClassifier(interpreter)
    }
    private val tfinterpreter: Interpreter by lazy {
        Interpreter(ResHolder.getDigitsClassifierModel(), InterpreterOptions(2, useNNAPI = true), this)
    }
    private val tfDigitClassifier: TFDigitClassifier by lazy {
        TFDigitClassifier(tfinterpreter, lifecycleScope)
    }

    @Suppress("UnnecessarySafeCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawView = findViewById(R.id.draw_view)
        drawView.setStrokeWidth(70.0f)
        drawView.setColor(Color.WHITE)
        drawView.setBackgroundColor(Color.BLACK)
        clearButton = findViewById(R.id.clear_button)
        predictedTextView = findViewById(R.id.predicted_text)
        predictedTFTextView = findViewById(R.id.predicted_text_tf)

        clearButton.setOnClickListener {
            drawView.clearCanvas()
            predictedTextView.text = "Please draw a digit"
            predictedTFTextView.text = "Please draw a digit"
        }

        drawView?.setOnTouchListener { _, event ->
            drawView?.onTouchEvent(event)

            if (event.action == MotionEvent.ACTION_UP) {
                classifyDrawing()
            }

            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        interpreter.close()
    }

    private fun classifyDrawing() {
        val rawBitmap = drawView.getBitmap()
        val bitmapToClassify = Bitmap.createScaledBitmap(
            rawBitmap,
            digitClassifier.inputImageWidth,
            digitClassifier.inputImageHeight,
            true
        )
        lifecycleScope.launch(Dispatchers.IO) {
            val input = BitmapConverter.convertBitmapToArray(bitmapToClassify)
            val results = digitClassifier.process(input)
            withContext(Dispatchers.Main) {
                val result = results.first()
                predictedTextView.text = "Digit: ${result.digit}\nIndex: ${result.index}\nPercent: ${result.percent}"
            }
        }
        val input = BitmapConverter.convertBitmapToByteBuffer(tfDigitClassifier, bitmapToClassify)
        tfDigitClassifier.classifyAsync(input) {
            runOnUiThread {
                predictedTFTextView.text = it
            }
        }
    }


}
