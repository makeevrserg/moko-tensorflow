/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.tensorflow

import dev.icerock.moko.resources.FileResource
import kotlinx.cinterop.ExperimentalForeignApi

@Suppress("ForbiddenComment")
@OptIn(ExperimentalForeignApi::class)
class ObjCInterpreter(
    override val fileResource: FileResource,
    override val options: ObjCInterpreterOptions
) : Interpreter {

    private val tflInterpreter: PlatformInterpreter = errorHandled { errPtr ->
        PlatformInterpreter(fileResource.path, options.tflInterpreterOptions, errPtr)
    }!!

    /**
     * Gets the number of input tensors.
     */
    override fun getInputTensorCount(): Int {
        return tflInterpreter.inputTensorCount().toInt()
    }

    /**
     * Gets the number of output Tensors.
     */
    override fun getOutputTensorCount(): Int {
        return tflInterpreter.outputTensorCount().toInt()
    }

    /**
     * Gets the Tensor associated with the provdied input index.
     *
     * @throws IllegalArgumentException if [index] is negative or is not smaller than the
     * number of model inputs.
     */
    override fun getInputTensor(index: Int): ObjCTensor {
        return errorHandled { errPtr ->
            tflInterpreter.inputTensorAtIndex(index.toULong(), errPtr)
        }!!.toObjCTensor()
    }

    /**
     * Gets the Tensor associated with the provdied output index.
     *
     * @throws IllegalArgumentException if [index] is negative or is not smaller than the
     * number of model inputs.
     */
    override fun getOutputTensor(index: Int): ObjCTensor {
        return errorHandled { errPtr ->
            tflInterpreter.outputTensorAtIndex(index.toULong(), errPtr)
        }!!.toObjCTensor()
    }

    /**
     * Resizes [index] input of the native model to the given [shape].
     */
    override fun resizeInput(index: Int, shape: TensorShape) {
        errorHandled { errPtr ->
            tflInterpreter.resizeInputTensorAtIndex(
                index.toULong(),
                shape.getNSNumberDimensionList(),
                errPtr
            )
        }
    }

    override fun allocateTensors() {
        errorHandled { errPtr ->
            tflInterpreter.allocateTensorsWithError(errPtr)
        }
    }

    override fun run(inputs: Map<Int, NativeInput>, outputs: MutableMap<Int, Any>) {
        require(inputs.size <= getInputTensorCount()) {
            "Wrong inputs dimension."
        }
        errorHandled { errorPointer ->
            tflInterpreter.allocateTensorsWithError(errorPointer)
            inputs.forEach {
                val tflTensor = tflInterpreter.inputTensorAtIndex(it.key.toULong(), errorPointer)!!
                tflTensor.copyData(it.value.nsData, errorPointer)
            }
            tflInterpreter.invokeWithError(errorPointer)
            outputs.keys.forEach {
                val outputTensor = getOutputTensor(it)
                val data = outputTensor.platformTensor.dataWithError(errorPointer)!!
                outputs[it] = data
            }
        }
    }

    /**
     * Release resources associated with the [ObjCInterpreter].
     *
     * ObjectiveC interpreter doesn't have jvm-like close method
     */
    override fun close() = Unit
}
