plugins {
    alias(libs.plugins.android.application)
    //Agregamos el plugin de Kotlin Serialization
    kotlin("plugin.serialization") version "1.9.24"
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
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "src/main/keepRules/rules.keep"
            )
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
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.viewpager2)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    //Aqui agregamos las librerias retrofit y kotlin serialization
    // Librería principal de Kotlinx Serialization Json
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Convertidor oficial de Retrofit para Kotlinx Serialization
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0")

    // Retrofit & OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // Para imágenes
    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Corrutinas de Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}