

# Android Ads SDK


## Setup Gradle

<h3>All gradle files must be .gradle.kts</h3>

Modified your root (project level) ```settings.gradle.kts``` as belowed code:
```groovy
dependencyResolutionManagement {    
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)    
  repositories {    
      google()    
      mavenCentral()    
      maven(url = "https://jitpack.io")    
      maven(url = "https://android-sdk.is.com/")    
      maven(url = "https://cboost.jfrog.io/artifactory/chartboost-ads/")    
      maven(url = "https://imobile-maio.github.io/maven")    
      maven(url = "https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")    
      maven(url = "https://artifact.bytedance.com/repository/pangle")    
      maven(url = "https://s3.amazonaws.com/smaato-sdk-releases/")    
      maven(url = "https://aa-sdk.s3-eu-west-1.amazonaws.com/android_repo")    
      maven(url = "https://sdk.tapjoy.com/")    
    }
}  

...  
  
include(":utils")  
  
  
```   



Now add the dependency to your app ```build.gradle.kts```:

```groovy

android {
    ...

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
            buildConfigField("int", "VERSION_CODE", "${defaultConfig.versionCode}")
            buildConfigField("String", "VERSION_NAME", "\"${defaultConfig.versionName}\"")
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
            )
            buildConfigField("int", "VERSION_CODE", "${defaultConfig.versionCode}")
            buildConfigField("String", "VERSION_NAME", "\"${defaultConfig.versionName}\"")
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    
    ...
}

dependencies {
    api(project(mapOf("path" to ":utils")))
}   

```  


Must have to add bellowed code in ```AndroidManifest.xml``` before the ```</application>``` :
```xml  

<!-- Admob -->
    <meta-data
        android:name="com.google.android.gms.ads.APPLICATION_ID"
        android:value="ca-app-pub-3940256099942544~3347511713" />
    <meta-data
        android:name="com.google.android.gms.ads.AD_MANAGER_APP"
        android:value="true" />
    
    
    <!-- IrnSrc -->
    <activity
        android:name="com.ironsource.sdk.controller.ControllerActivity"
        android:configChanges="orientation|screenSize"
        android:hardwareAccelerated="true" />
    <activity
        android:name="com.ironsource.sdk.controller.InterstitialActivity"
        android:configChanges="orientation|screenSize"
        android:hardwareAccelerated="true"
        android:theme="@android:style/Theme.Translucent" />
    <activity
        android:name="com.ironsource.sdk.controller.OpenUrlActivity"
        android:configChanges="orientation|screenSize"
        android:hardwareAccelerated="true"
        android:theme="@android:style/Theme.Translucent" />
    
    <provider
        android:name="com.ironsource.lifecycle.IronsourceLifecycleProvider"
        android:authorities="${applicationId}.IronsourceLifecycleProvider" />

 ```  

## Setup SplashActivity.kt
splash screen name should be "SplashActivity.kt"

```SplashActivity.kt```
```kotlin   
class SplashActivity : CustomActivity(BuildConfig.VERSION_CODE),  
  CustomActivity.OnFetchDataListener {

	override fun onCreate(savedInstanceState: Bundle?) {  
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_splash)


		super.onFetchDataListener = this
		com.facebook.ads.AdSettings.setDataProcessingOptions(arrayOf<String>())
		com.facebook.ads.AudienceNetworkAds.initialize(applicationContext)
		com.google.android.gms.ads.MobileAds.initialize(this)
        	

	}
	
	override fun onDataLoadSuccess() {  
		val intent = Intent(this@SplashActivity, MainActivity::class.java)
		startActivity(intent)  
		finish()
	}
}
```


```MyApplication.kt```
```kotlin

//Must add MyApplication class to name on <application> in Manifest

class MyApplication : Application() {  
  
  override fun onCreate() {  
	  super.onCreate()  
	  ...
	  //To show App open ads
	  com.adsutils.AppOpenManager(this)  
	  ...
  }  
}

```


in other activities..

``` MainActivity.kt```

```kotlin
class MainActivity : CustomActivity() {

	// Banner & Native ads must call in onStart
	override fun onStart() {  
		super.onStart()  
		//For banner Ads  NOTE: Banner Ad Must Call before Native Ad.
		showBannerAds(findViewById(R.id.adView))
		//For Native Ad
		showNativeAds(findViewById(R.id.nativeAdView))  
		//For small native Ad
		showNativeSmallAds(findViewById(R.id.smallNativeAdView))  
	}

	override fun onCreate(savedInstanceState: Bundle?) {  
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		
		// interstital ads (Repalce this@MainActivity with your current activity and NextActivity with you preffered activity)
		val i = Intent(this@MainActivity, NextActivity::class.java)
		startAdsActivity(i)


		// Setting the Adapter with the Large Native Ad  
		recyclerview.adapter = setAdsToAdapter(adapter)
	
		// Setting the Adapter with the Small Native Ad  
		recyclerview.adapter = setSmallAdsToAdapter(adapter)



		// to show rewarded..
		showRewardAds(this@MainActivity, object : AdsManager.OnRewardAdsListener {  
		  
		  override fun onRewardEarned() {  
			  Toast.makeText(this@MainActivity,"Reward Earned Success Fully",Toast.LENGTH_SHORT).show()  
		  }  
		  
		  override fun onRewardedError() {  
			  Toast.makeText(this@MainActivity,"Error in loading ads",Toast.LENGTH_SHORT).show()  
		  }
		  
		})


	}	
}

```




