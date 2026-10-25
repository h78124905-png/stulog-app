plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.yourname.stulogplus"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.yourname.stulogplus"
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "1.1.0"
    }

    // On GitHub Actions, explicitly use the cached keystore. The Android
    // Gradle Plugin's implicit debug key was not matching the cached file.
    signingConfigs {
        create("ciDebug") {
            storeFile = file("${System.getenv("HOME") ?: System.getProperty("user.home")}/.android/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        getByName("debug") {
            if (System.getenv("CI") == "true") {
                signingConfig = signingConfigs.getByName("ciDebug")
            }
        }
    }

    sourceSets["main"].java.setSrcDirs(listOf("src/main/kotlin"))

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {}
