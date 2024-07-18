// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    val kotlin_version = "1.9.10"

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
    }
}

plugins {
    id("com.android.application") version "8.5.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.dagger.hilt.android") version "2.48.1" apply false
    id ("org.jetbrains.kotlin.multiplatform") version "1.8.10" apply false
    id ("org.jetbrains.kotlin.plugin.serialization") version "1.8.10" apply false
}