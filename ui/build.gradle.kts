plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.compose.compiler.get().pluginId)
}

android {
    namespace = "com.eugenics.ui"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }

    buildFeatures {
        compose = true
    }
    composeCompiler {
        enableStrongSkippingMode = true
    }
}

dependencies {
    // Modules
    implementation(project(":core"))
    implementation(project(":ui_core"))
    implementation(project(":resource"))

    //Libs
    implementation(files("libs/material-colors-util.jar"))

    implementation(libs.androidx.ktx)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.google.material)

    // Compose
    implementation(libs.bundles.compose)

    // Compose Accompanist
    implementation(libs.bundles.accomponist)

    // Material 3
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.windowsize)
    implementation(libs.androidx.material3.adaptiv)

    //Coil compose
    implementation(libs.coil)

    testImplementation(libs.junit)
    androidTestImplementation(libs.junitExt)
    androidTestImplementation(libs.espesso)
}