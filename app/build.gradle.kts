plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
}

android {
    buildFeatures {
        viewBinding = true
    }
    namespace = "com.example.bmi"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.bmi"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.animation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    ksp(libs.androidx.room.compiler)
    implementation(libs.gson)
    implementation(libs.glide)

    implementation(libs.media3.exoplayer)
    implementation(libs.androidx.media3.session)
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation(libs.media3.ui)
    implementation(libs.koin.android)

    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}