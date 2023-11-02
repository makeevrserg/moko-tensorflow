/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

rootProject.name = "moko-tensorflow"
pluginManagement {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()

        // for moko-media dependency MaterialFilePicker
        maven("https://jitpack.io")
    }
    versionCatalogs { create("klibs") { from(files("./gradle/klibs.versions.toml")) } }
}

enableFeaturePreview("VERSION_CATALOGS")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":tensorflow")
include(":sample:android-app")
include(":sample:mpp-library")
