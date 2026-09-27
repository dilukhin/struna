plugins {
    id("com.android.application")
}

android {
    namespace = "com.dilukhin.struna"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.dilukhin.struna"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            optimization {
                enable = true
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
