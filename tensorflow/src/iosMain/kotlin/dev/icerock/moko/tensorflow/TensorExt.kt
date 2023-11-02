/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.tensorflow

import cocoapods.TensorFlowLiteObjC.TFLTensor
import platform.Foundation.NSNumber

internal fun TFLTensor.toObjCTensor() = ObjCTensor(this)

internal fun TensorShape.getNSNumberDimensionList() = dimensions.map(::NSNumber)
