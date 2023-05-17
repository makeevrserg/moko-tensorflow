package dev.icerock.moko.tensorflow.maping.tensor

import dev.icerock.moko.tensorflow.Tensor
import dev.icerock.moko.tensorflow.TensorDataType

/**
 * This Mapper is required to convert ObjC output tensor values into kotlin representation
 * @param dataType shared data type of the output
 * @see Float32TensorDataMapper
 */
sealed class OutputTensorDataMapper<T>(val dataType: TensorDataType) {

    abstract fun map(outputTensor: Tensor): T
}