plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.ksp.get().pluginId)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
    id(libs.plugins.kotlinx.serialization.get().pluginId)
    id(libs.plugins.dagger.hilt.get().pluginId)
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
    // modules
    implementation(project(":core"))
    implementation(project(":core_data"))

    // AndroidX
    implementation(libs.androidx.ktx)
    implementation(libs.bundles.coroutines)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.junitExt)
    androidTestImplementation(libs.espesso)

    // Android media player
    implementation(libs.androidx.media)

    // Exoplayer
    implementation(libs.exoplayer)
    implementation(libs.exoplayer.extOkhttp)
    implementation(libs.exoplayer.extMediasession)

    // Glide dependencies
    implementation(libs.glide)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Media3
    implementation(libs.bundles.meida3)

    // Jackson
    implementation(libs.jackson.databind)
}