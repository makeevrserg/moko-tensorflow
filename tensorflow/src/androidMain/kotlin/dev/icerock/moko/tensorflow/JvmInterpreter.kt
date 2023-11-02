/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.tensorflow

import android.content.Context
import dev.icerock.moko.resources.FileResource

class JvmInterpreter(
    override val fileResource: FileResource,
    override val options: JvmInterpreterOptions,
    context: Context
) : Interpreter {

    private val tensorFlowInterpreter = PlatformInterpreter(
        fileResource.openAsFile(context),
        options.tensorFlowInterpreterOptions
    )

    /**
     * Gets the number of input tensors.
     */
    override fun getInputTensorCount(): Int = tensorFlowInterpreter.inputTensorCount

    /**
     * Gets the number of output Tensors.
     */
    override fun getOutputTensorCount(): Int = tensorFlowInterpreter.outputTensorCount

    /**
     * Gets the Tensor associated with the provdied input index.
     *
     * @throws IllegalArgumentException if [index] is negative or is not smaller than the
     * number of model inputs.
     */
    override fun getInputTensor(index: Int): JvmTensor {
        return tensorFlowInterpreter.getInputTensor(index).toJvmTensor()
    }

    /**
     * Gets the Tensor associated with the provdied output index.
     *
     * @throws IllegalArgumentException if [index] is negative or is not smaller than the
     * number of model inputs.
     */
    override fun getOutputTensor(index: Int): JvmTensor {
        return tensorFlowInterpreter.getOutputTensor(index).toJvmTensor()
    }

    /**
     * Resizes [index] input of the native model to the given [shape].
     */
    override fun resizeInput(index: Int, shape: TensorShape) {
        tensorFlowInterpreter.resizeInput(index, shape.dimensions)
    }

    override fun allocateTensors() {
        tensorFlowInterpreter.allocateTensors()
    }

    /**
     * Runs model inference if the model takes multiple inputs, or returns multiple outputs.
     */
    override fun run(inputs: Map<Int, NativeInput>, outputs: MutableMap<Int, Any>) {
        val anyInputs = inputs
            .toList()
            .associate { (k, v) -> k to v.byteBuffer }
        tensorFlowInterpreter.runForMultipleInputsOutputs(Array(anyInputs.size) { i -> anyInputs[i] }, outputs)
    }

    /**
     * Release resources associated with the [JvmInterpreter].
     */
    override fun close() {
        tensorFlowInterpreter.close()
    }
}
