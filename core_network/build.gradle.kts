plugins {
    androidLibrary
    kotlin
    kapt
    parcelize
    serialization
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
    kotlinOptions {
        jvmTarget = AppConfig.jvmTarget
    }
}

dependencies {

    // AndroidX
    implementation(Deps.AndroidX.coreKtx)
    implementation(Deps.AndroidX.appcompat)

    // Retrofit
    implementation(Deps.Retrofit.retrofit)
    implementation(Deps.Retrofit.gsonConverter)
    implementation(Deps.Retrofit.serializationConverter)
    implementation(Deps.Retrofit.intercepter)

    // Hilt
    implementation(Deps.Hilt.hilt)
    kapt(Deps.Hilt.compiler)

    // Dagger
    implementation(Deps.Dagger.dagger)
    kapt(Deps.Dagger.compiler)

    //Modules
    implementation(project(":core"))
}