/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.tensorflow

import dev.icerock.moko.resources.FileResource

interface Interpreter {
    val fileResource: FileResource
    val options: InterpreterOptions

    /**
     * Gets the number of input tensors.
     */
    fun getInputTensorCount(): Int

    /**
     * Gets the number of output Tensors.
     */
    fun getOutputTensorCount(): Int

    /**
     * Gets the Tensor associated with the provdied input index.
     *
     * @throws IllegalArgumentException if [index] is negative or is not smaller than the
     * number of model inputs.
     */
    fun getInputTensor(index: Int): Tensor

    /**
     * Gets the Tensor associated with the provdied output index.
     *
     * @throws IllegalArgumentException if [index] is negative or is not smaller than the
     * number of model inputs.
     */
    fun getOutputTensor(index: Int): Tensor

    /**
     * Resizes [index] input of the native model to the given [shape].
     */
    fun resizeInput(index: Int, shape: TensorShape)
    fun allocateTensors()

    /**
     * Runs model inference with native input data
     *
     * @param inputs - map where value is NSData or java's ByteBuffer
     * @param outputs - required output array
     */
    fun run(inputs: Map<Int, NativeInput>, outputs: MutableMap<Int, Any>)

    /**
     * Release resources associated with the [Interpreter].
     */
    fun close()
}
