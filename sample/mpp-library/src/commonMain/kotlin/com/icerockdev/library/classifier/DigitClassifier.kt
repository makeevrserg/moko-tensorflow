package com.icerockdev.library.classifier

import com.icerockdev.library.classifier.DigitClassifier.Input
import com.icerockdev.library.classifier.DigitClassifier.Output

/**
 * DigitClassifier - Classifies number from 0 to 10
 *
 * It will pass [Nx28x28x1] and return [Nx10] Float array where N is batch size
 */
interface DigitClassifier : Classifier<Input, Output> {
    /**
     * This function will run [Classifier.classify] and return DTO state [MappedResult]
     */
    fun process(input: Input): List<MappedResult>
    val inputImageWidth: Int
    val inputImageHeight: Int

    class Input(
        val batchSize: Int,
        val array: Array<Array<FloatArray>> = Array(batchSize) {
            Array(28) { FloatArray(28) }
        }
    )

    class Output(
        val batchSize: Int,
        val rawArray: Array<FloatArray> = Array(batchSize) { FloatArray(10) },
    )

    data class MappedResult(
        val digit: Digit = Digit.ERROR,
        val percent: Float = -1f,
        val index: Int = -1
    )

    enum class Digit {
        ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, ERROR
    }

    companion object : Classifier.Options {
        override fun inputShape(batchSize: Int): IntArray {
            return intArrayOf(batchSize, 28, 28)
        }

        override val inputTensorIndex: Int = 0
    }
}
