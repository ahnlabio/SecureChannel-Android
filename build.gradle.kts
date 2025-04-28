// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    val kotlin_version = "2.1.10"

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
    }
}

plugins {
    id("com.android.application") version "8.9.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.20" apply false
    id("com.google.dagger.hilt.android") version "2.56" apply false
    id ("org.jetbrains.kotlin.multiplatform") version "1.8.10" apply false
    id ("org.jetbrains.kotlin.plugin.serialization") version "2.1.10" apply false
    id ("com.google.devtools.ksp") version "2.1.10-1.0.31" apply false
}