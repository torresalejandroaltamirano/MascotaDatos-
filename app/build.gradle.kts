plugins { id("com.android.application") }

android {
    signingConfigs {
        create("release") {
            val ks = System.getenv("MASCOTADATOS_KEYSTORE_PATH")
            if (!ks.isNullOrBlank()) {
                storeFile = file(ks)
                storePassword = System.getenv("MASCOTADATOS_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("MASCOTADATOS_KEY_ALIAS")
                keyPassword = System.getenv("MASCOTADATOS_KEY_PASSWORD")
            }
        }
    }
    namespace = "cl.mascotadatos.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "cl.mascotadatos.app"
        minSdk = 24
        targetSdk = 36
        versionCode = 140
        versionName = "1.3.10"
    }

    buildTypes {
        release {
            if (!System.getenv("MASCOTADATOS_KEYSTORE_PATH").isNullOrBlank()) {
                signingConfig = signingConfigs.getByName("release")
            }
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

dependencies {
    implementation("androidx.core:core:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.activity:activity:1.10.1")
    implementation("androidx.webkit:webkit:1.17.0")
}
