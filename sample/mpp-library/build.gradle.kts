@file:Suppress("UnusedPrivateMember")

import org.jetbrains.kotlin.konan.target.KonanTarget
import ru.astrainteractive.gradleplugin.util.ProjectProperties.projectInfo

/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("dev.icerock.mobile.multiplatform-resources")
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
        name = "MultiPlatformLibrary"
        framework {
            baseName = "MultiPlatformLibrary"
            isStatic = false
            export(projects.tensorflow)
        }
        pod(
            name = "TensorFlowLiteObjC",
            linkOnly = true,
            moduleName = "TFLTensorFlowLite",
            version = "2.12.0"
        )
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.mokoResources)
                implementation(libs.kotlinStdLib)
                implementation(libs.coroutines)
                implementation(libs.mokoResources)
                implementation(libs.mokoMedia)
                api(projects.tensorflow)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.appCompat)
                api(libs.tensorflowLite)
            }
        }
        val iosX64Main by getting {
            resources.srcDirs("build/generated/moko/iosX64Main/src")
        }
        val iosArm64Main by getting {
            resources.srcDirs("build/generated/moko/iosArm64Main/src")
        }
        val iosSimulatorArm64Main by getting {
            resources.srcDirs("build/generated/moko/iosSimulatorArm64Main/src")
        }
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "dev.icerock.moko.sample.tensorflowtest"
}

android {
    namespace = "com.icerockdev.library"

    sourceSets {
        getByName("main").java.srcDirs("build/generated/moko/androidMain/src")
    }
}

kotlin.targets
    .filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>()
    .flatMap { it.binaries }
    .filterIsInstance<org.jetbrains.kotlin.gradle.plugin.mpp.Framework>()
    .forEach { framework ->
        val isIosDevice =
            framework.compilation.konanTarget == org.jetbrains.kotlin.konan.target.KonanTarget.IOS_ARM64
        val xcFramework =
            project.file("./../ios-app/Pods/TensorFlowLiteC/Frameworks/TensorFlowLiteC.xcframework/")
        val frameworkDir = if (isIosDevice) {
            File(xcFramework, "ios-arm64")
        } else {
            File(xcFramework, "ios-arm64_x86_64-simulator")
        }

        framework.linkerOpts(
            frameworkDir.path.let { "-F$it" },
            "-framework",
            "TensorFlowLiteC"
        )
    }
