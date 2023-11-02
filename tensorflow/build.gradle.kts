@file:Suppress("UnusedPrivateMember")

import ru.astrainteractive.gradleplugin.util.ProjectProperties.projectInfo

/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(klibs.plugins.klibs.gradle.java.core)
    alias(klibs.plugins.klibs.gradle.android.core)
    alias(klibs.plugins.klibs.gradle.publication)
    kotlin("native.cocoapods")
}

kotlin {
    targetHierarchy.default()

    androidTarget()
    ios()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = projectInfo.name
        homepage = projectInfo.url
        version = projectInfo.versionString
        ios.deploymentTarget = "16.0"
        source = "https://github.com/icerockdev/moko-tensorflow.git"
        framework {
            baseName = "ml"
            isStatic = false
        }

        pod("TensorFlowLiteObjC") {
            moduleName = "TFLTensorFlowLite"
            version = "2.12.0"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.mokoResources)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.appCompat)
                api(libs.tensorflowLite)
            }
        }
    }
}

android {
    namespace = "dev.icerock.moko.tensorflow"
}
