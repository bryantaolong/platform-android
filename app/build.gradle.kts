plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // 如果使用 Room 或 Dagger 等需要注解处理的库，可以取消注释 kapt 插件
    // id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.bryan.platform" // 使用您提供的命名空间
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bryan.platform_android" // 使用您提供的应用ID
        minSdk = 24 // 保持您提供的 minSdk 版本
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11 // 保持您提供的 Java 版本
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11" // 保持您提供的 JVM 目标版本
    }
    // 启用 View Binding，方便在 Activity/Fragment 中访问 UI 元素
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // AndroidX 核心库 (您已存在的部分)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Retrofit2 网络请求库 (新增)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Retrofit2 Gson 转换器 (新增)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp 日志拦截器，用于查看网络请求日志 (调试用) (新增)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Kotlin 协程支持 (用于异步操作，如果后续需要使用协程可以取消注释)
    // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // 单元测试和 UI 测试库 (您已存在的部分)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}