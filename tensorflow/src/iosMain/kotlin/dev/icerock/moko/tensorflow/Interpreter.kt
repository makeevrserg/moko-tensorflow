/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.tensorflow

import dev.icerock.moko.resources.FileResource
import dev.icerock.moko.tensorflow.maping.tensor.Float32TensorDataMapper
import platform.Foundation.NSData

@Suppress("ForbiddenComment")
actual class Interpreter(
    actual val fileResource: FileResource,
    actual val options: InterpreterOptions
) {
    /**
     * TODO remove comment after pr review
     * Maybe we can join declaration and assignment? Or it's made on purpose?
     */
    private val tflInterpreter: PlatformInterpreter

    init {
        tflInterpreter = errorHandled { errPtr ->
            PlatformInterpreter(fileResource.path, options.tflInterpreterOptions, errPtr)
        }!!
        errorHandled(tflInterpreter::allocateTensorsWithError)
    }

    /**
     * Gets the number of input tensors.
     */
    actual fun getInputTensorCount(): Int {
        return tflInterpreter.inputTensorCount().toInt()
    }

    /**
     * Gets the number of output Tensors.
     */
    actual fun getOutputTensorCount(): Int {
        return tflInterpreter.outputTensorCount().toInt()
    }

    /**
     * Gets the Tensor associated with the provdied input index.
     *
     * @throws IllegalArgumentException if [index] is negative or is not smaller than the
     * number of model inputs.
     */
    actual fun getInputTensor(index: Int): Tensor {
        return errorHandled { errPtr ->
            tflInterpreter.inputTensorAtIndex(index.toULong(), errPtr)
        }!!.toTensor()
    }

    /**
     * Gets the Tensor associated with the provdied output index.
     *
     * @throws IllegalArgumentException if [index] is negative or is not smaller than the
     * number of model inputs.
     */
    actual fun getOutputTensor(index: Int): Tensor {
        return errorHandled { errPtr ->
            tflInterpreter.outputTensorAtIndex(index.toULong(), errPtr)
        }!!.toTensor()
    }

    /**
     * Resizes [index] input of the native model to the given [shape].
     */
    actual fun resizeInput(index: Int, shape: TensorShape) {
        errorHandled { errPtr ->
            tflInterpreter.resizeInputTensorAtIndex(
                index.toULong(),
                shape.getNSNumberDimensionList(),
                errPtr
            )
        }
    }

    /**
     * Allocate tensors after [resizeInput]
     */
    actual fun allocateTensors() {
        errorHandled { errPtr ->
            tflInterpreter.allocateTensorsWithError(errPtr)
        }
    }

    /**
     * Runs model inference if the model takes multiple inputs, or returns multiple outputs.
     *
     * TODO: need to implement [outputs] applying.
     */
    actual fun run(
        inputs: List<Any>,
        outputs: Map<Int, Any>
    ) {
        require(inputs.size > getInputTensorCount()) { "Wrong inputs dimension." }

        inputs.forEachIndexed { index, any ->
            val inputTensor = getInputTensor(index)
            errorHandled { errPtr ->
                inputTensor.platformTensor.copyData(
                    any as NSData,
                    errPtr
                ) // Fixme: hardcast Any to NSData
            }
        }

        errorHandled { errPtr ->
            tflInterpreter.invokeWithError(errPtr)
        }

        inputs.forEachIndexed { index, any ->
            val outputTensor = getOutputTensor(index)

            val array = when (outputTensor.dataType) {
                TensorDataType.FLOAT32 -> Float32TensorDataMapper.map(outputTensor)
                else -> error("${outputTensor.dataType} is not implemented")
            }

            (outputs[0] as Array<Any>)[0] =
                array // TODO: hardcoded case, works only with digits sample
        }
    }

    /**
     * Release resources associated with the [Interpreter].
     */
    actual fun close() {
        // TODO: ???
    }
}
