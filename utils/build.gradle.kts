plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk = 33
    namespace = "com"

    defaultConfig {
        minSdk = 24
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

    implementation ("androidx.core:core-ktx:1.10.1")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    api ("com.google.android.gms:play-services-ads:22.4.0")
    api ("com.facebook.android:audience-network-sdk:6.15.0")

    api("com.google.android.gms:play-services-ads-identifier:18.0.1")

    api("com.google.android.gms:play-services-appset:16.0.2")
    api("com.google.android.gms:play-services-ads-identifier:18.0.1")
    api("com.google.android.gms:play-services-basement:18.2.0")
//    api "com.android.support:customtabs:25.2.0"

    api ("androidx.lifecycle:lifecycle-extensions:2.2.0")
    api ("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    //noinspection LifecycleAnnotationProcessorWithJava8
    annotationProcessor ("androidx.lifecycle:lifecycle-compiler:2.6.1")

    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.google.code.gson:gson:2.10.1")

    api ("com.ironsource.sdk:mediationsdk:7.4.0")

// Add AdColony Network
    implementation ("com.ironsource.adapters:adcolonyadapter:4.3.15")
    implementation ("com.adcolony:sdk:4.8.0")

// Add Applovin Network
    implementation ("com.ironsource.adapters:applovinadapter:4.3.39")
    implementation ("com.applovin:applovin-sdk:11.11.2")

// Add APS Network
    implementation("com.amazon.android:aps-sdk:9.8.4")
    implementation("com.ironsource.adapters:apsadapter:4.3.9")

// Add Chartboost Network
    implementation ("com.ironsource.adapters:chartboostadapter:4.3.12")
    implementation ("com.chartboost:chartboost-sdk:9.3.1")

// Add Fyber Network (Adapter only)
    implementation ("com.ironsource.adapters:fyberadapter:4.3.27")
    implementation ("com.fyber:marketplace-sdk:8.2.3")

// Add Facebook Network
    implementation ("com.ironsource.adapters:facebookadapter:4.3.44")
    implementation ("com.facebook.android:audience-network-sdk:6.15.0")

// Add AdMob and Ad Manager Network
    implementation ("com.google.android.gms:play-services-ads:22.2.0")
    implementation ("com.ironsource.adapters:admobadapter:4.3.39")

// Add HyprMX Network
    implementation ("com.ironsource.adapters:hyprmxadapter:4.3.5")
    implementation ("com.hyprmx.android:HyprMX-SDK:6.2.0")


// Add InMobi Network
    implementation ("com.ironsource.adapters:inmobiadapter:4.3.18")
    implementation ("com.inmobi.monetization:inmobi-ads-kotlin:10.5.7")

// Add Vungle Network
    implementation ("com.ironsource.adapters:vungleadapter:4.3.21")
    implementation ("com.vungle:publisher-sdk-android:6.12.1")

// Add Maio Network
    implementation ("com.ironsource.adapters:maioadapter:4.1.11")
    implementation ("com.maio:android-sdk:1.1.16@aar")
    implementation ("com.ironsource.adapters:mintegraladapter:4.3.17")

//overseas market
    implementation ("com.mbridge.msdk.oversea:mbbid:16.4.61")
    implementation ("com.mbridge.msdk.oversea:reward:16.4.61")
    implementation ("com.mbridge.msdk.oversea:mbbanner:16.4.61")
    implementation ("com.mbridge.msdk.oversea:newinterstitial:16.4.61")

// Add myTarget Network
    implementation ("com.ironsource.adapters:mytargetadapter:4.1.17")
    implementation ("com.my.target:mytarget-sdk:5.18.0")
    implementation ("com.google.android.exoplayer:exoplayer:2.19.0")

// Add Pangle Network
    implementation ("com.ironsource.adapters:pangleadapter:4.3.21")
    implementation ("com.pangle.global:ads-sdk:5.4.0.2")

// Add Smaato Network
    implementation ("com.ironsource.adapters:smaatoadapter:4.3.9")
    implementation ("com.smaato.android.sdk:smaato-sdk-banner:22.0.2")
    implementation ("com.smaato.android.sdk:smaato-sdk-in-app-bidding:22.0.2")


// Add SuperAwesome Network
    implementation ("com.ironsource.adapters:superawesomeadapter:4.1.7")
    implementation ("tv.superawesome.sdk.publisher:superawesome:9.1.0")

// Add Tapjoy Network
    implementation ("com.ironsource.adapters:tapjoyadapter:4.1.25")
    implementation ("com.tapjoy:tapjoy-android-sdk:13.0.1")

// Add UnityAds Network
    implementation ("com.ironsource.adapters:unityadsadapter:4.3.31")
    implementation ("com.unity3d.ads:unity-ads:4.8.0")
}