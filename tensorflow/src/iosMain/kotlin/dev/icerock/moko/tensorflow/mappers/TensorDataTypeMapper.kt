package dev.icerock.moko.tensorflow.mappers

import dev.icerock.moko.tensorflow.PlatformTensor
import dev.icerock.moko.tensorflow.TensorDataType

interface TensorDataTypeMapper<T> {
    val type: TensorDataType
    fun map(tensor: PlatformTensor): T
}