package com.icerockdev.library.classifier

import com.icerockdev.library.classifier.maping.DigitMapper
import dev.icerock.moko.tensorflow.Interpreter
import dev.icerock.moko.tensorflow.TensorShape

class DefaultDigitClassifier(
    private val interpreter: Interpreter
) : DigitClassifier {
    private val digitMapper: DigitMapper = DigitMapper

    override val inputImageWidth: Int by lazy {
        val inputShape = interpreter.getInputTensor(DigitClassifier.inputTensorIndex).shape
        inputShape[1]
    }
    override val inputImageHeight: Int by lazy {
        val inputShape = interpreter.getInputTensor(DigitClassifier.inputTensorIndex).shape
        inputShape[2]
    }

    override fun process(input: DigitClassifier.Input): List<DigitClassifier.MappedResult> {
        val output = classify(input)
        return output.rawArray.indices.map { i ->
            digitMapper.map(output, i)
        }
    }

    override fun classify(input: DigitClassifier.Input): DigitClassifier.Output {
        val batchIndex = DigitClassifier.inputTensorIndex
        val inputShape = DigitClassifier.inputShape(input.batchSize)
        interpreter.resizeInput(batchIndex, inputShape.let(::TensorShape))
        interpreter.allocateTensors()

        val output = DigitClassifier.Output(input.batchSize)
        interpreter.run(listOf(input.array), mapOf(DigitClassifier.inputTensorIndex to output.rawArray))
        return output
    }
}