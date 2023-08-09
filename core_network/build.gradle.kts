plugins {
    androidLibrary
    kotlin
    kapt
    parcelize
    serialization
    dagger
}

android {
    namespace = "com.eugenics.core_network"
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
    kotlinOptions {
        jvmTarget = BaseConfig.jvmTarget
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
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Dagger
    implementation(Deps.Dagger.dagger)
    kapt(Deps.Dagger.compiler)

    //Modules
    implementation(project(":core"))
}