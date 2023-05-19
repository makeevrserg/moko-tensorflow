/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.tensorflow

import dev.icerock.moko.tensorflow.maping.ObjcDataTypeMapper
import platform.Foundation.NSNumber

actual class Tensor(
    internal val platformTensor: PlatformTensor
) {
    actual val dataType: TensorDataType
        get() = platformTensor.dataType.let(ObjcDataTypeMapper::map)

    actual val name: String
        get() = platformTensor.name()

    actual val shape: IntArray
        get() {
            val rawShapeArr = errorHandled { errPtr ->
                platformTensor.shapeWithError(errPtr)
            } as List<NSNumber>

            return rawShapeArr.map {
                it.unsignedIntValue().toInt()
            }.toIntArray()
        }
}
