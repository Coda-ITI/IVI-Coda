plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "android.vendor.coda.observation"
    compileSdk = 36

    defaultConfig {
        applicationId = "android.vendor.coda.observation"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        useLibrary("android.car")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        jvmTarget = "11"
    }
    buildFeatures {
        aidl = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.fragment:fragment-ktx:1.7.0") // For fragment-based split-screen
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")

    implementation("com.github.skydoves:colorpickerview:2.2.4")
    implementation("androidx.cardview:cardview:1.0.0")

//    implementation("com.github.ibrahimsn98:speedometer:1.0.1")
    implementation("com.github.anastr:speedviewlib:1.6.1")
}