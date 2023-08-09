plugins {
    androidLibrary
    kotlin
    kapt
    dagger
}

android {
    namespace = "com.eugenics.core_database"
    compileSdk = BaseConfig.compileSdk

    defaultConfig {
        minSdk = BaseConfig.minSdk

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kapt {
        arguments { arg("room.schemaLocation", "$projectDir/schemas") }
        correctErrorTypes = true
    }

    kotlinOptions {
        jvmTarget = BaseConfig.jvmTarget
    }
}

dependencies {

    // AndroidX
    implementation(libs.androidx.ktx)
    implementation(libs.androidx.appcompat)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Dagger
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    //Room
    kapt(libs.room.compiler)
    implementation(libs.room.ktx)
    testImplementation(libs.room.testing)
    implementation(libs.room.paging)

    // Core module
    implementation(project(":core"))
}