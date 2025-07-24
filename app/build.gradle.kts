plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "io.qzz.studyhard.mail"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.qzz.studyhard.mail"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        
        multiDexEnabled = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/DEPENDENCIES"
        }
    }
    kotlin {
        jvmToolchain(17)
    }
    
    // 添加更多内存给Kotlin编译器
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn")
        }
    }
}

// 全局配置加速构建
tasks.withType<JavaCompile>().configureEach {
    options.isFork = true
    options.isIncremental = false
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
    implementation(composeBom)
    
    implementation("androidx.multidex:multidex:2.0.1")
    
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-core")
    
    // Markdown 处理
    implementation("com.github.noties:Markwon:4.6.2")  // 核心库
    implementation("com.github.noties.Markwon:html:4.6.2")
    implementation("com.github.noties.Markwon:image:4.6.2")
    implementation("com.github.noties.Markwon:syntax-highlight:4.6.2")
    
    // PDF 导出
    implementation("com.itextpdf:itext7-core:7.2.5")
    
    // HTML 转换
    implementation("org.commonmark:commonmark:0.21.0")
    implementation("org.commonmark:commonmark-ext-gfm-tables:0.21.0")
    
    // 文件操作
    implementation("androidx.documentfile:documentfile:1.0.1")
    
    // 协程
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    debugImplementation("androidx.compose.ui:ui-tooling")
}