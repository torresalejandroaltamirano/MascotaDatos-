plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "cl.mascotadatos.app"
    compileSdk = 36

    signingConfigs {
        create("stableDebug") {
            storeFile = rootProject.file("ci/mascotadatos-debug.keystore")
            storePassword = "mascotadatos2026"
            keyAlias = "mascotadatos"
            keyPassword = "mascotadatos2026"
        }
    }

    defaultConfig {
        applicationId = "cl.mascotadatos.app.v35final"
        minSdk = 24
        targetSdk = 36
        versionCode = 40
        versionName = "1.3.3-instalable"
    }

    buildTypes {
        getByName("debug") {
            signingConfig = signingConfigs.getByName("stableDebug")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.activity:activity-ktx:1.10.1")
    implementation("androidx.webkit:webkit:1.17.0")
    implementation("com.google.android.material:material:1.13.0")
}
