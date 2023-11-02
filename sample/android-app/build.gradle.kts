
import ru.astrainteractive.gradleplugin.util.ProjectProperties.projectInfo

/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    id("kotlin-android")
    alias(libs.plugins.android.application)
    alias(klibs.plugins.klibs.gradle.java.core)
    alias(klibs.plugins.klibs.gradle.android.core)
}

android {
    namespace = "com.icerockdev"
    dexOptions {
        javaMaxHeapSize = "2g"
    }

    defaultConfig {
        applicationId = "dev.icerock.moko.samples.tensorflow"
        versionCode = 1
        versionName = projectInfo.versionString
        setProperty("archivesBaseName", "${projectInfo.name}-${projectInfo.versionString}")
    }
}

dependencies {
    implementation(libs.coroutines)
    implementation(libs.kotlinStdLib)
    implementation(libs.coreKtx)
    implementation(libs.appCompat)
    implementation(libs.constraintLayout)
    implementation(libs.androidDraw)
    implementation(libs.playServices)
    implementation(libs.mokoResources)
    implementation(libs.lifecycleRuntime)

    implementation(projects.sample.mppLibrary)
}
