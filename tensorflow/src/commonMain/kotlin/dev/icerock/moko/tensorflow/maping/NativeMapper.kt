package dev.icerock.moko.tensorflow.maping

import dev.icerock.moko.tensorflow.TensorDataType

/**
 * This interface is required to map native tflite DataType into moko's shared [TensorDataType]
 */
interface NativeMapper<NATIVE : Any> {
    fun map(nativeObject: NATIVE): TensorDataType
    fun unmap(sharedObject: TensorDataType): NATIVE
}