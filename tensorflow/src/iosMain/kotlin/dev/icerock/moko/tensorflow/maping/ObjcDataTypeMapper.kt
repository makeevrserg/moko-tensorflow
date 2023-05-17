package dev.icerock.moko.tensorflow.maping

import cocoapods.TFLTensorFlowLite.TFLTensorDataType
import dev.icerock.moko.tensorflow.TensorDataType

/**
 * ObjectiveC tflite [TFLTensorDataType] mapping into moko's [TensorDataType]
 */
object ObjcDataTypeMapper : NativeMapper<TFLTensorDataType> {
    override fun map(nativeObject: TFLTensorDataType): TensorDataType = when (nativeObject) {
        TFLTensorDataType.TFLTensorDataTypeFloat32 -> TensorDataType.FLOAT32
        TFLTensorDataType.TFLTensorDataTypeInt32 -> TensorDataType.INT32
        TFLTensorDataType.TFLTensorDataTypeUInt8 -> TensorDataType.UINT8
        TFLTensorDataType.TFLTensorDataTypeInt64 -> TensorDataType.INT64
        TFLTensorDataType.TFLTensorDataTypeInt16 -> TensorDataType.INT16
        TFLTensorDataType.TFLTensorDataTypeInt8 -> TensorDataType.INT8

        TFLTensorDataType.TFLTensorDataTypeBool,
        TFLTensorDataType.TFLTensorDataTypeFloat16,
        TFLTensorDataType.TFLTensorDataTypeNoType,
        TFLTensorDataType.TFLTensorDataTypeFloat64 -> {
            throw IllegalArgumentException("$nativeObject not supported.")
        }

        else -> throw IllegalArgumentException("unknown TFLTensorDataType - $this")
    }

    override fun unmap(sharedObject: TensorDataType): TFLTensorDataType = when (sharedObject) {
        TensorDataType.FLOAT32 -> TFLTensorDataType.TFLTensorDataTypeFloat32
        TensorDataType.INT32 -> TFLTensorDataType.TFLTensorDataTypeInt32
        TensorDataType.UINT8 -> TFLTensorDataType.TFLTensorDataTypeUInt8
        TensorDataType.INT64 -> TFLTensorDataType.TFLTensorDataTypeInt64
        TensorDataType.INT16 -> TFLTensorDataType.TFLTensorDataTypeInt16
        TensorDataType.INT8 -> TFLTensorDataType.TFLTensorDataTypeInt8
    }
}
