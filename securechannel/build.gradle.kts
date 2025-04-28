import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("kotlinx-serialization")
    id("maven-publish")
    id("com.google.devtools.ksp")
}

val mGroupId = "io.myabcwallet"
val mArtifactId = "securechannel"
val mVersionCode = 13
val mVersionName = "0.1.13"
val libraryName = "SecureChannel-Android"
val libraryDescription = "Library for Android to create a Secure Channel to communicate with WAAS"

val properties: Properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())
val serverAuthUrl: String = properties.getProperty("server.auth.url")
val stgServerAuthUrl: String = properties.getProperty("stg.server.auth.url")
val devServerAuthUrl: String = properties.getProperty("dev.server.auth.url")

android {
    namespace = "io.myabcwallet.securechannel"
    compileSdk = 35

    defaultConfig {
        minSdk = 29

        buildConfigField("String", "SERVER_AUTH_URL", serverAuthUrl)
        buildConfigField("String", "STG_SERVER_AUTH_URL", stgServerAuthUrl)
        buildConfigField("String", "DEV_SERVER_AUTH_URL", devServerAuthUrl)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("com.google.dagger:hilt-android:2.56")
    ksp("com.google.dagger:hilt-compiler:2.56")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.1")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.79")
}

publishing {
    publications {
        register<MavenPublication>("gpr") {
            groupId = mGroupId
            artifactId = mArtifactId
            version = mVersionName
            artifact("$buildDir/outputs/aar/${mArtifactId}-release.aar")
        }
    }

    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/ahnlabio/SecureChannel-Android")
            credentials {
                username = System.getenv("GPR_USR")
                password = System.getenv("GPR_KEY")
            }
        }
    }
}