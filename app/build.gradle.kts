plugins {
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "cl.mascotadatos.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "cl.mascotadatos.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1313
        versionName = "1.3.13"
    }

    buildTypes {
        getByName("debug") {
            manifestPlaceholders["admobAppId"] = "ca-app-pub-3940256099942544~3347511713"
            buildConfigField("String", "BANNER_AD_UNIT_ID", "\"ca-app-pub-3940256099942544/6300978111\"")
        }
        getByName("release") {
            manifestPlaceholders["admobAppId"] = "ca-app-pub-2369519530952827~6693315056"
            buildConfigField("String", "BANNER_AD_UNIT_ID", "\"ca-app-pub-2369519530952827/7526305566\"")
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
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

kotlin { jvmToolchain(17) }

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.04.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.google.android.gms:play-services-ads:24.7.0")
    implementation("com.google.android.ump:user-messaging-platform:4.0.0")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.activity:activity-ktx:1.10.1")
    implementation("androidx.webkit:webkit:1.17.0")
    implementation("com.google.android.material:material:1.13.0")
}
