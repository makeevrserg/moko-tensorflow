package com.icerockdev.library

import dev.icerock.moko.tensorflow.Interpreter
import dev.icerock.moko.tensorflow.NativeInput
import dev.icerock.moko.tensorflow.toUByteArray
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.posix.memcpy

/**
 * This wrapper modifies ObjectiveC output into project-specific Array<FloatArray>
 */
class DigitInterpreter(private val instance: Interpreter) : Interpreter by instance {
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
    override fun run(inputs: Map<Int, NativeInput>, outputs: MutableMap<Int, Any>) {
        instance.run(inputs, outputs)
        val nsData = outputs[0] as NSData
        outputs[0] = arrayOf(nsData.toUByteArray().toFloatArray())
    }
}