/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.tensorflow

import dev.icerock.moko.tensorflow.maping.JavaDataTypeMapper

actual class Tensor(
    internal val platformTensor: PlatformTensor
) {
    actual val dataType: TensorDataType
        get() = platformTensor.dataType().let(JavaDataTypeMapper::map)
    actual val name: String
        get() = platformTensor.name()
    actual val shape: IntArray
        get() = platformTensor.shape()
}
