package dev.icerock.moko.tensorflow.maping

import dev.icerock.moko.tensorflow.TensorDataType
import org.tensorflow.lite.DataType

/**
 * Java tflite [DataType] mapping into moko's [TensorDataType]
 */
object JavaDataTypeMapper : NativeMapper<DataType> {
    override fun map(nativeObject: DataType): TensorDataType = when (nativeObject) {
        DataType.FLOAT32 -> TensorDataType.FLOAT32
        DataType.INT32 -> TensorDataType.INT32
        DataType.UINT8 -> TensorDataType.UINT8
        DataType.INT64 -> TensorDataType.INT64
        DataType.INT8 -> TensorDataType.INT8
        DataType.INT16 -> TensorDataType.INT16

        DataType.BOOL,
        DataType.STRING -> {
            throw IllegalArgumentException("$nativeObject not supported.")
        }
    }

    override fun unmap(sharedObject: TensorDataType): DataType = when (sharedObject) {
        TensorDataType.FLOAT32 -> DataType.FLOAT32
        TensorDataType.INT32 -> DataType.INT32
        TensorDataType.UINT8 -> DataType.UINT8
        TensorDataType.INT64 -> DataType.INT64
        TensorDataType.INT16 -> DataType.INT16
        TensorDataType.INT8 -> DataType.INT8
    }

}