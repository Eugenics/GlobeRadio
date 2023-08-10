plugins {
    alias(libs.plugins.android.library)
    kotlin
    kapt
    dagger
    serialization
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
    implementation(Deps.AndroidX.appcompat)

    // Tests
    testImplementation(Deps.JUnit.junit)
    androidTestImplementation(Deps.JUnit.junitExt)


    // Hilt
    implementation(Deps.Hilt.hilt)
    kapt(Deps.Hilt.compiler)

    // Dagger
    implementation(Deps.Dagger.dagger)
    kapt(Deps.Dagger.compiler)

    // KotlinX Serialization
    implementation(Deps.KotlinX.kotlinxSerialization)

    // Kotlin coroutines test
    testImplementation(Deps.KotlinX.kotlinxCoroutinesTest)

    // Modules
    implementation(project(":core"))
    implementation(project(":core_database"))
    implementation(project(":core_network"))
}