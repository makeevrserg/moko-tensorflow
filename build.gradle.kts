/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

plugins {
    alias(libs.plugins.moko.resources) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.application) apply false
    // klibs - core
    alias(klibs.plugins.klibs.gradle.detekt) apply true
    alias(klibs.plugins.klibs.gradle.detekt.compose) apply false
    alias(klibs.plugins.klibs.gradle.dokka.root) apply false
    alias(klibs.plugins.klibs.gradle.dokka.module) apply false
    alias(klibs.plugins.klibs.gradle.java.core) apply false
    alias(klibs.plugins.klibs.gradle.stub.javadoc) apply false
    alias(klibs.plugins.klibs.gradle.publication) apply false
    alias(klibs.plugins.klibs.gradle.rootinfo) apply false
    // klibs - android
    alias(klibs.plugins.klibs.gradle.android.core) apply false
    alias(klibs.plugins.klibs.gradle.android.compose) apply false
    alias(klibs.plugins.klibs.gradle.android.apk.sign) apply false
    alias(klibs.plugins.klibs.gradle.android.apk.name) apply false
    alias(klibs.plugins.klibs.gradle.android.publication) apply false
}

/**
 * This function will delete every ./build folder
 * ./gradlew :cleanProject
 */
tasks.register("cleanProject", Delete::class) {
    fun clearProject(project: Project) {
        project.childProjects.values.forEach(::clearProject)
        delete(project.buildDir)
    }
    clearProject(rootProject)
}
