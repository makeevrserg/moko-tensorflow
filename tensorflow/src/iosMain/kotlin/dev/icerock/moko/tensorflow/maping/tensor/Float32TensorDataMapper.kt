package dev.icerock.moko.tensorflow.maping.tensor

import dev.icerock.moko.tensorflow.Tensor
import dev.icerock.moko.tensorflow.TensorDataType
import dev.icerock.moko.tensorflow.errorHandled
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.posix.memcpy

/**
 * Mapper for [TensorDataType.FLOAT32] type
 */
object Float32TensorDataMapper : OutputTensorDataMapper<FloatArray>(TensorDataType.FLOAT32) {

    private fun NSData.toUByteArray(): UByteArray = UByteArray(this.length.toInt()).apply {
        usePinned {
            memcpy(it.addressOf(0), this@toUByteArray.bytes, this@toUByteArray.length)
        }
    }

    private fun UByteArray.toFloatArray(): FloatArray {
        @Suppress("MagicNumber")
        val floatArr = FloatArray(this.size / 4)
        usePinned { src ->
            floatArr.usePinned { dst ->
                memcpy(dst.addressOf(0), src.addressOf(0), this.size.toULong())
            }
        }
        return floatArr
    }

    override fun map(outputTensor: Tensor): FloatArray {
        return errorHandled { errPtr ->
            outputTensor.platformTensor.dataWithError(errPtr)
        }!!.toUByteArray().toFloatArray()
    }
}
