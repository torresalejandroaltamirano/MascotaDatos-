plugins {
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val admobLiveAdsEnabled = providers.gradleProperty("admobLiveAdsEnabled").orNull == "true"
val sampleAdMobAppId = "ca-app-pub-3940256099942544~3347511713"
val productionAdMobAppId = "ca-app-pub-2369519530952827~6693315056"
val sampleBannerAdUnitId = "ca-app-pub-3940256099942544/6300978111"
val productionBannerAdUnitId = "ca-app-pub-2369519530952827/7526305566"
val releaseAdMobAppId = if (admobLiveAdsEnabled) productionAdMobAppId else sampleAdMobAppId
val releaseBannerAdUnitId = if (admobLiveAdsEnabled) productionBannerAdUnitId else sampleBannerAdUnitId

android {
    namespace = "cl.mascotadatos.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "cl.mascotadatos.app"
        minSdk = 24
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "SUPABASE_PUBLIC_KEY", "\"sb_publishable_cI2DN3hLz4NxFiWZoEIRig_fYVXcZ4Z\"")
        manifestPlaceholders["admobAppId"] = sampleAdMobAppId
        versionCode = 1316
        versionName = "1.3.16"
    }

    buildTypes {
        getByName("debug") {
            manifestPlaceholders["admobAppId"] = sampleAdMobAppId
            buildConfigField("String", "BANNER_AD_UNIT_ID", "\"$sampleBannerAdUnitId\"")
        }
        getByName("release") {
            manifestPlaceholders["admobAppId"] = releaseAdMobAppId
            buildConfigField("String", "BANNER_AD_UNIT_ID", "\"$releaseBannerAdUnitId\"")
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
    implementation(project(":recovery"))
    implementation("androidx.activity:activity-compose:1.10.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.04.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
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
