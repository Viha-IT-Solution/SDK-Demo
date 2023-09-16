pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
//        maven { url = "https://jitpack.io" }
        maven(url = "https://jitpack.io")
        maven(url = "https://android-sdk.is.com/")
        maven(url = "https://cboost.jfrog.io/artifactory/chartboost-ads/")
        maven(url = "https://imobile-maio.github.io/maven")
        maven(url = "https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
        maven(url = "https://artifact.bytedance.com/repository/pangle")
        maven(url = "https://s3.amazonaws.com/smaato-sdk-releases/")
        maven(url = "https://aa-sdk.s3-eu-west-1.amazonaws.com/android_repo")
        maven(url = "https://sdk.tapjoy.com/")

//        maven { "https://android-sdk.is.com/" }
//        maven { "https://cboost.jfrog.io/artifactory/chartboost-ads/" }
//        maven { "https://imobile-maio.github.io/maven" }
//        maven { "https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea" }
//        maven { "https://artifact.bytedance.com/repository/pangle" }
//        maven { "https://s3.amazonaws.com/smaato-sdk-releases/" }
//        maven { "https://aa-sdk.s3-eu-west-1.amazonaws.com/android_repo" }
//        maven { "https://sdk.tapjoy.com/" }
    }
}

rootProject.name = "SDK Demo"
include(":app")
include(":utils")
