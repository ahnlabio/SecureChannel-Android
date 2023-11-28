import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id ("kotlin-kapt")
    id ("com.google.dagger.hilt.android")
    id ("kotlinx-serialization")
    id ("maven-publish")
}

val mGroupId = "io.myabcwallet"
val mArtifactId = "securechannel"
val mVersionCode = 1
val mVersionName = "0.1.0"
val libraryName = "SecureChannel"
val libraryDescription = "Library for Android to create a Secure Channel to communicate with WAAS"

val properties: Properties = Properties()
properties.load(project.rootProject.file("gradle.properties").inputStream())
val serverAuthUrl: String = properties.getProperty("server.auth.url")
val devServerAuthUrl: String = properties.getProperty("dev.server.auth.url")
val authAccessKey: String = properties.getProperty("auth.access.key")
val authAccessSecret: String = properties.getProperty("auth.access.secret")

android {
    namespace = "io.myabcwallet.securechannel"
    compileSdk = 34

    defaultConfig {
        minSdk = 29

        buildConfigField("String", "SERVER_AUTH_URL", serverAuthUrl)
        buildConfigField("String", "DEV_SERVER_AUTH_URL", devServerAuthUrl)
        buildConfigField("String", "AUTH_ACCESS_KEY", authAccessKey)
        buildConfigField("String", "AUTH_ACCESS_SECRET", authAccessSecret)

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
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-compiler:2.48.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation ("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.76")
}

tasks.register<Jar>("androidSourcesJar") {
    archiveClassifier.set("sources")
    from(android.sourceSets.getByName("main").java.srcDirs)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = mGroupId
                artifactId = mArtifactId
                version = mVersionName

                from(components["release"])
                artifact(tasks["androidSourcesJar"])

                pom {
                    name.set(libraryName)
                    description.set(libraryDescription)
                }
            }
        }

        repositories {
            maven {
                name = "GithubPackages"
                url = uri("https://maven.pkg.github.com/ahnlabio/SecureChannel-Android")
                credentials {
                    username = System.getenv("GPR_USER")
                    password = System.getenv("GPR_API_KEY")
                }
            }
        }
    }
}

tasks.named("publish") {
    dependsOn("assemble")
}