plugins {
    androidLibrary
    kotlin
    kapt
    parcelize
    serialization
    dagger
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    namespace = "com.eugenics.media_service"

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
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

    // Android media player
    implementation(Deps.AndroidX.media)

    // Exoplayer
    implementation(Deps.Exoplayer.exoplayer)
    implementation(Deps.Exoplayer.extOkhttp)
    implementation(Deps.Exoplayer.extMediasession)

    // Glide dependencies
    implementation(Deps.Images.glide)

    // Hilt
    implementation(Deps.Hilt.hilt)
    kapt(Deps.Hilt.compiler)

    // modules
    implementation(project(":core"))
    implementation(project(":core_data"))
}