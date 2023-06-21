plugins {
    androidLibrary
    kotlin
    parcelize
    serialization
}

android {
    namespace = "com.eugenics.core"
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
    // KotlinX Serialization
    implementation(Deps.KotlinX.kotlinxSerialization)
    //GSON
    implementation(Deps.Google.gson)
}