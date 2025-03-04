plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.mavenPublish)
}

android {
    namespace = "com.yuu.labelprinter"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

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
        jvmTarget = "11"
    }
    sourceSets{
        getByName("main"){
            aidl.srcDir(file("/src/main/aidl"))
        }
    }
    buildFeatures {
        aidl = true
    }
}

dependencies {
    api(fileTree("libs/gprintersdkv22.jar"))
    api(fileTree("libs/jcc-bate-0.7.3.jar"))
    api(fileTree("libs/ksoap2-android-assembly-2.5.2-jar-with-dependencies.jar"))
    api(fileTree("libs/xUtils-2.6.14.jar"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
afterEvaluate{
    publishing{
        publications {
            create<MavenPublication>("release"){
                group = "com.yuu.android.component"
                artifactId = "LabelPrinter"
                version = "0.0.1"
                from(components["release"])
            }
        }
    }
}

