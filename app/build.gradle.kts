plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.dcdhameliya.sdkdemo"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.dcdhameliya.sdkdemo"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


//    Mandatory for onesignal, flurry, ads & SDK

    implementation(project(mapOf("path" to ":utils")))

//    implementation("androidx.annotation:annotation:1.6.0")
//    implementation("com.facebook.android:audience-network-sdk:6.+") {
//        exclude(group = "com.android.support" , module = "support-v4")
//    }
//    implementation("com.flurry.android:analytics:14.3.0@aar")
//    implementation("com.onesignal:OneSignal:5.0.0")
//    implementation("com.google.android.gms:play-services-ads:22.2.0")
//    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
//    implementation("com.google.android.play:core-ktx:1.8.1")
//    implementation("com.ironsource.sdk:mediationsdk:7.4.0")


}