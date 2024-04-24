plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.ksp.get().pluginId)
    id(libs.plugins.dagger.hilt.get().pluginId)
    id(libs.plugins.kotlinx.serialization.get().pluginId)
}

android {
    namespace = "com.eugenics.core_data"
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
}

dependencies {
    // AndroidX
    implementation(libs.androidx.ktx)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.junitExt)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // KotlinX Serialization
    implementation(libs.kotlinx.serialization)

    // Kotlin coroutines test
    testImplementation(libs.kotlinx.coroutines.test)

    // Modules
    implementation(project(":core"))
    implementation(project(":core_database"))
    implementation(project(":core_network"))
}