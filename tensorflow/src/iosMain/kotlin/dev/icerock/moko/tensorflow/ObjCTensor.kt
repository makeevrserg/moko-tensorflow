/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.tensorflow

import cocoapods.TensorFlowLiteObjC.TFLTensorDataType
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSNumber

@OptIn(ExperimentalForeignApi::class)
class ObjCTensor(
    internal val platformTensor: PlatformTensor
) : Tensor {
    override val dataType: TensorDataType
        get() = platformTensor.dataType.toTensorDataType()

    override val name: String
        get() = platformTensor.name()

    override val shape: IntArray
        get() {
            val rawShapeArr = errorHandled { errPtr ->
                platformTensor.shapeWithError(errPtr)
            } as List<NSNumber>

            return rawShapeArr.map {
                it.unsignedIntValue().toInt()
            }.toIntArray()
        }
}

private fun TFLTensorDataType.toTensorDataType() = when (this) {
    TFLTensorDataType.TFLTensorDataTypeFloat32 -> TensorDataType.FLOAT32
    TFLTensorDataType.TFLTensorDataTypeInt32 -> TensorDataType.INT32
    TFLTensorDataType.TFLTensorDataTypeUInt8 -> TensorDataType.UINT8
    TFLTensorDataType.TFLTensorDataTypeInt64 -> TensorDataType.INT64
    TFLTensorDataType.TFLTensorDataTypeInt16 -> TensorDataType.INT16
    TFLTensorDataType.TFLTensorDataTypeInt8 -> TensorDataType.INT8
    TFLTensorDataType.TFLTensorDataTypeBool ->
        throw IllegalArgumentException("TFLTensorDataTypeFloat16 not supported.")

    TFLTensorDataType.TFLTensorDataTypeFloat16 ->
        throw IllegalArgumentException("TFLTensorDataTypeFloat16 not supported.")

    TFLTensorDataType.TFLTensorDataTypeNoType ->
        throw IllegalArgumentException("TFLTensorDataTypeNoType: wrong tensor type.")

    else -> throw IllegalArgumentException("unknown TFLTensorDataType - $this")
}
