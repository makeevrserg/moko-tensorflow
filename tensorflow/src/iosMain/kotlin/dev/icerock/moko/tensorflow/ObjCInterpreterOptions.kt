/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.tensorflow

import cocoapods.TensorFlowLiteObjC.TFLInterpreterOptions

class ObjCInterpreterOptions(override val numThreads: Int) : InterpreterOptions {

    internal val tflInterpreterOptions = TFLInterpreterOptions().apply {
        setNumberOfThreads(numThreads.toULong())
    }
}
