plugins {
    androidLibrary
    kotlin
    kapt
    parcelize
    serialization
    dagger
}

android {
    namespace = "com.eugenics.media3_service"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {

    // AndroidX
    implementation(Deps.AndroidX.coreKtx)
    implementation(Deps.AndroidX.appcompat)
    implementation(Deps.KotlinX.coroutinesCore)
    implementation(Deps.KotlinX.coroutinesAndroid)

    // Tests
    testImplementation(Deps.JUnit.junit)
    androidTestImplementation(Deps.JUnit.junitExt)
    androidTestImplementation(Deps.Espresso.core)

    // Media3
    implementation(Deps.Media3.media3Exo)
    implementation(Deps.Media3.media3UI)
    implementation(Deps.Media3.media3MediaSession)
    implementation(Deps.Media3.media3Dash)
    implementation(Deps.Media3.media3Hls)
    implementation(Deps.Media3.media3Exo)
    implementation(Deps.Media3.media3Okhttp)

    // Glide dependencies
    implementation(Deps.Images.glide)

    // Hilt
    implementation(Deps.Hilt.hilt)
    kapt(Deps.Hilt.compiler)

    // modules
    implementation(project(":core"))
    implementation(project(":core_data"))
}