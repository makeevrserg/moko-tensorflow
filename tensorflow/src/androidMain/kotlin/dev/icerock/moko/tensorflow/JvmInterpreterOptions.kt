/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.tensorflow

class JvmInterpreterOptions(
    override val numThreads: Int,
    useNNAPI: Boolean = false,
    allowFp16PrecisionForFp32: Boolean = false,
    allowBufferHandleOutput: Boolean = false,
    delegates: List<Delegate> = emptyList()
) : InterpreterOptions {

    internal val tensorFlowInterpreterOptions = PlatformInterpreterOptions()
        .setNumThreads(numThreads)
        .setAllowBufferHandleOutput(allowBufferHandleOutput)
        .setAllowFp16PrecisionForFp32(allowFp16PrecisionForFp32)
        .setUseNNAPI(useNNAPI)
        .apply {
            delegates.forEach {
                addDelegate(PlatformTensorFlowDelegate { it.getNativeHandle() })
            }
        }
}
