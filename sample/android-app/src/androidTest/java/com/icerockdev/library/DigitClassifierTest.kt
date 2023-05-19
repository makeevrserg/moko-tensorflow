package com.icerockdev.library

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.icerockdev.BitmapConverter
import com.icerockdev.library.classifier.DefaultDigitClassifier
import com.icerockdev.library.classifier.DigitClassifier
import dev.icerock.moko.tensorflow.Interpreter
import dev.icerock.moko.tensorflow.InterpreterOptions
import java.io.InputStream
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DigitClassifierTest {
    @Throws(Exception::class)
    private fun loadBitmapFromAssets(fileName: String): Bitmap? {
        val assetManager: AssetManager = InstrumentationRegistry.getInstrumentation().context.assets
        val inputStream: InputStream = assetManager.open(fileName)
        return BitmapFactory.decodeStream(inputStream)
    }

    private fun withClassifier(block: (DigitClassifier) -> Unit) {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val tfliteModel = ResHolder.getDigitsClassifierModel()
        val options = InterpreterOptions(1)
        val interpreter = Interpreter(context = appContext, fileResource = tfliteModel, options = options)
        val classifier = DefaultDigitClassifier(interpreter)
        block.invoke(classifier)
        interpreter.close()
    }

    private fun createInput(batchSize: Int, init: (Int) -> Float = { 0f }): DigitClassifier.Input {
        return Array(batchSize) {
            Array(28) {
                FloatArray(28, init)
            }
        }.let { DigitClassifier.Input(batchSize, it) }
    }

    @Test
    fun TEST_single_input_RETURN_right_value(): Unit = withClassifier { classifier ->
        val batchSize = 1
        val input = createInput(batchSize)
        val array = classifier.classify(input).rawArray
        assertEquals(array.size, batchSize)
        assertEquals(array[0].size, 10)
    }

    @Test
    fun TEST_multiple_input_RETURN_right_value(): Unit = withClassifier { classifier ->
        val batchSize = 5
        val input = createInput(batchSize)
        val array = classifier.classify(input).rawArray
        assertEquals(array.size, batchSize)
        for (i in 0 until array.size - 1) {
            val firstSum = array[i].sum()
            val secondSum = array[i + 1].sum()
            assertEquals(firstSum, secondSum)
            assertEquals(array[i].size, 10)
        }
    }

    @Test
    fun TEST_asset_image_RETURN_valid(): Unit = withClassifier { classifier ->
        mapOf(
            DigitClassifier.Digit.ZERO to loadBitmapFromAssets("zero.png")!!,
            DigitClassifier.Digit.ONE to loadBitmapFromAssets("one.png")!!,
            DigitClassifier.Digit.EIGHT to loadBitmapFromAssets("eight.png")!!,
        ).forEach { (expectDigit, bitmap) ->
            val input = BitmapConverter.convertBitmapToArray(bitmap)
            val actualOutput = classifier.process(input).first()
            assertEquals(expectDigit, actualOutput.digit)
        }
    }
}