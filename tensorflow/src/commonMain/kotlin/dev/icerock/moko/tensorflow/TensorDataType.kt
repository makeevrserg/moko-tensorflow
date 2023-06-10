/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.tensorflow

@Suppress("MagicNumber")
enum class TensorDataType(val value: Int) {
    FLOAT32(1);

    fun byteSize(): Int = when (this) {
        FLOAT32 -> 4
    }
}
