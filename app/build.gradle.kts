plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.memorycat"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.memorycat"
        minSdk = 30
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        enable = true
    }
    dataBinding {
        enable = true
    }
}

dependencies {
    implementation("de.hdodenhof:circleimageview:3.1.0") // for circle profile
    implementation("androidx.core:core-ktx:1.12.0")
    //implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation(platform("com.google.firebase:firebase-bom:32.4.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx:22.2.0")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("com.google.firebase:firebase-firestore-ktx:24.9.1")
    implementation("com.google.firebase:firebase-storage-common:17.0.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    //implementation("androidx.fragment:fragment-ktx:1.3.6")
    implementation("androidx.fragment:fragment-ktx:1.3.6")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
}