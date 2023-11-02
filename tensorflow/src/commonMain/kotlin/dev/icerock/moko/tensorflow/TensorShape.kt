package dev.icerock.moko.tensorflow

import kotlin.jvm.JvmInline

@JvmInline
value class TensorShape(
    val dimensions: IntArray
)
