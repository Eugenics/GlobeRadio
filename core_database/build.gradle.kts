plugins {
    androidLibrary
    kotlin
    kapt
    dagger
}

android {
    namespace = AppConfig.namespace
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk

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
        jvmTarget = AppConfig.jvmTarget
    }
}

dependencies {

    // AndroidX
    implementation(Deps.AndroidX.coreKtx)
    implementation(Deps.AndroidX.appcompat)

    // Hilt
    implementation(Deps.Hilt.hilt)
    kapt(Deps.Hilt.compiler)

    // Dagger
    implementation(Deps.Dagger.dagger)
    kapt(Deps.Dagger.compiler)

    //Room
    kapt(Deps.Room.compiler)
    implementation(Deps.Room.room)
    testImplementation(Deps.Room.testing)
    implementation(Deps.Room.paging)

    // Core module
    implementation(project(":core"))
}