/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.tensorflow

import org.tensorflow.lite.DataType

class JvmTensor(
    internal val platformTensor: PlatformTensor
) : Tensor {
    override val dataType: TensorDataType
        get() = platformTensor.dataType().toTensorDataType()
    override val name: String
        get() = platformTensor.name()
    override val shape: IntArray
        get() = platformTensor.shape()
}

private fun DataType.toTensorDataType() = when (this) {
    DataType.FLOAT32 -> TensorDataType.FLOAT32
    DataType.INT32 -> TensorDataType.INT32
    DataType.UINT8 -> TensorDataType.UINT8
    DataType.INT64 -> TensorDataType.INT64
    DataType.INT8 -> TensorDataType.INT8
    DataType.INT16 -> TensorDataType.INT16
    DataType.BOOL ->
        throw IllegalArgumentException("BOOL not supported.")

    DataType.STRING ->
        throw IllegalArgumentException("STRING not supported.")
}
