plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val admobLiveAdsEnabled = providers.gradleProperty("admobLiveAdsEnabled").orNull == "true"
val sampleAdMobAppId = "ca-app-pub-3940256099942544~3347511713"
val productionAdMobAppId = "ca-app-pub-2369519530952827~6693315056"

android {
    namespace = "cl.mascotadatos.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "cl.mascotadatos.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 1311
        versionName = "1.3.11"
        manifestPlaceholders["adMobAppId"] = sampleAdMobAppId
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            manifestPlaceholders["adMobAppId"] = sampleAdMobAppId
            buildConfigField("boolean", "ADMOB_LIVE_ADS_ENABLED", "false")
        }
        release {
            manifestPlaceholders["adMobAppId"] = if (admobLiveAdsEnabled) productionAdMobAppId else sampleAdMobAppId
            buildConfigField("boolean", "ADMOB_LIVE_ADS_ENABLED", admobLiveAdsEnabled.toString())
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
    implementation("com.google.android.gms:play-services-ads:25.5.0")
    implementation("com.google.android.ump:user-messaging-platform:4.0.0")
}
