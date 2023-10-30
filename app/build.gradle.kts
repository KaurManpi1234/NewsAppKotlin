plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")

    id ("dagger.hilt.android.plugin")
}

android {
    namespace = "com.example.newsappkotlin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.newsappkotlin"
        minSdk = 33
        //noinspection EditedTargetSdkVersion
        targetSdk = 34
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
        buildFeatures {
            viewBinding =true
        }

    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}
configurations.all {
    resolutionStrategy {
        force ("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.airbnb.android:lottie:3.4.0")
    implementation("androidx.collection:collection-ktx:1.3.0")

    //ui
    implementation("com.google.android.material:material:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.test.ext:junit-ktx:1.1.5")
    //navigation
   val navigation = "2.7.4"
    implementation ("androidx.navigation:navigation-fragment-ktx:$navigation")
    implementation ("androidx.navigation:navigation-ui-ktx:$navigation")
    implementation("androidx.fragment:fragment-ktx:1.6.1")

    // lifecycle components
   val lifecycle = "2.6.2"
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle")
    implementation ("androidx.lifecycle:lifecycle-common-java8:$lifecycle")
    implementation ("androidx.lifecycle:lifecycle-extensions:2.2.0")

    //retrofit and moshi converter
    val retrofit = "2.9.0"
  val moshi = "1.15.0"
    implementation ("com.squareup.retrofit2:retrofit:$retrofit")
    implementation ("com.squareup.retrofit2:converter-moshi:$retrofit")
    implementation ("com.squareup.moshi:moshi:$moshi")

    //noinspection KaptUsageInsteadOfKsp
    kapt ("com.squareup.moshi:moshi-kotlin-codegen:$moshi")

    // coroutine
    val coroutine = "1.7.1"
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutine")
    kapt ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutine")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.10")


    // Room components
   val room = "2.6.0"
    implementation ("androidx.room:room-ktx:$room")
    kapt ("androidx.room:room-compiler:$room")
    annotationProcessor ("androidx.room:room-compiler:$room")

    androidTestImplementation ("androidx.room:room-testing:$room")

    //hilt
 val hilt = "2.48.1"
    implementation ("com.google.dagger:hilt-android:$hilt")
    kapt ("com.google.dagger:hilt-compiler:$hilt")
    kapt ("com.google.dagger:hilt-android-compiler:$hilt")

    //glide
    val glide = "4.16.0"
    implementation ("com.github.bumptech.glide:glide:$glide")
    kapt ("com.github.bumptech.glide:compiler:$glide")

    //datastore
    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    //unit testing
    testImplementation("junit:junit:4.13.2")
    //integration testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}


