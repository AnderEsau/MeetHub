plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.equipo7.meethub"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.equipo7.meethub"
        minSdk = 28
        targetSdk = 37
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    // Retrofit2 y Converter Gson (Punto 1.3 Bitácora)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Glide (Para imágenes de salas)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Corrutinas de Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}