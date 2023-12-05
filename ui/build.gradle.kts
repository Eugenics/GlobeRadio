plugins {
    androidLibrary
    kotlin
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
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtensionVersion.get()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
}

dependencies {

    implementation(Deps.AndroidX.coreKtx)
    implementation(Deps.Google.material)
    implementation(Deps.Compose.composeUi)
    implementation(Deps.Compose.composeTooling)
    implementation(Deps.Compose.composePreview)
    implementation(Deps.Compose.animationGraphics)
    implementation(Deps.AndroidX.pallete)

    // Compose Accompanist
    implementation(Deps.Accomponist.inserts)
    implementation(Deps.Accomponist.coil)
    implementation(Deps.Accomponist.swipe)
    implementation(Deps.Accomponist.systemUiController)
    implementation(Deps.Accomponist.navigaionAnimation)

    //Coil compose
    implementation(Deps.Images.coil)

    // Material 3
    implementation(Deps.Material3.material)

    // Modules
    implementation(project(":resource"))
    implementation(project(":core"))
    implementation(project(":ui_core"))

    // Libs
    implementation(files("..\\libs\\material-colors-util.jar"))
}