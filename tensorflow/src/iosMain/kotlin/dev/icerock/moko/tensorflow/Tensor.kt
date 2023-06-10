/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.tensorflow

import cocoapods.TFLTensorFlowLite.TFLTensorDataType
import platform.Foundation.NSNumber

actual class Tensor(
    internal val platformTensor: PlatformTensor
) {
    actual val dataType: TensorDataType
        get() = platformTensor.dataType.toTensorDataType()

    actual val name: String
        get() = platformTensor.name()

    actual val shape: IntArray
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

    TFLTensorDataType.TFLTensorDataTypeBool,
    TFLTensorDataType.TFLTensorDataTypeFloat16,
    TFLTensorDataType.TFLTensorDataTypeNoType,
    TFLTensorDataType.TFLTensorDataTypeInt8,
    TFLTensorDataType.TFLTensorDataTypeInt16,
    TFLTensorDataType.TFLTensorDataTypeInt64,
    TFLTensorDataType.TFLTensorDataTypeUInt8,
    TFLTensorDataType.TFLTensorDataTypeInt32 -> error("$this not supported.")

    else -> throw IllegalArgumentException("unknown TFLTensorDataType - $this")
}
