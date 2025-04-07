plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("androidx.navigation.safeargs.kotlin")
    alias(libs.plugins.ksp)
    alias(libs.plugins.hiltAndroidGradlePlugin)
    id("kotlin-parcelize")
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.compose.compiler)
    jacoco
}

android {
    namespace = "com.example.skillcinema"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.skillcinema"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.example.skillcinema.CustomTestRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "2.1.10"
    }


}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.fragment)
    debugImplementation(libs.androidx.fragment.testing.manifest)
    androidTestImplementation(libs.androidx.fragment.testing)

    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    implementation(libs.viewpager2)
    implementation(libs.dotsindikator)

    implementation(libs.paging.runtime)

    implementation(libs.glide)

    implementation(libs.dagger.hilt.android)
    ksp(libs.hilt.compiller)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiller)
    testImplementation(libs.hilt.android.testing)
    kspTest(libs.hilt.compiller)

    implementation(libs.retrofit2)
    implementation(libs.retrofit2.moshi.converter)

    implementation(libs.moshi)
    ksp(libs.moshi.ksp)
    implementation(libs.moshi.kotlin)

    implementation(libs.parallax.scroll)

    implementation(libs.expandable.textview)

    implementation(libs.room.runtime)
    ksp(libs.room.runtime.ksp)
    implementation(libs.room.ktx)
    implementation(libs.room.rxjava)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material)
    implementation(libs.compose.preview)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    debugImplementation(libs.compose.preview.debug)
    androidTestImplementation(libs.compose.ui.tests)
    debugImplementation(libs.compose.ui.tests.debug)
    implementation(libs.compose.adaptive)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Unit testing
    testImplementation(libs.kotlinx.coroutines.test)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)

    androidTestImplementation(libs.kaspresso)
    androidTestImplementation(libs.kaspresso.compose.support)

    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)


    debugImplementation (libs.leakcanary.android)


}


tasks.withType<Test> {
    useJUnitPlatform()
    maxHeapSize = "1G"
    testLogging {
        events("passed", "skipped", "failed")
    }
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn(tasks["test"])

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*"
    )

    // Используем project.layout.buildDirectory для безопасного обращения к директориям сборки
    val kotlinClasses =
        fileTree(project.layout.buildDirectory.dir("tmp/kotlin-classes/debug").get().asFile) {
            exclude(fileFilter)
        }
    val javaClasses =
        fileTree(project.layout.buildDirectory.dir("intermediates/javac/debug").get().asFile) {
            exclude(fileFilter)
        }
    classDirectories.setFrom(files(kotlinClasses, javaClasses))

    val mainSrc = "$projectDir/src/main/java"
    val kotlinSrc = "$projectDir/src/main/kotlin"
    sourceDirectories.setFrom(files(mainSrc, kotlinSrc))

    executionData.setFrom(fileTree(project.layout.buildDirectory.get().asFile) {
        include("jacoco/test.exec", "outputs/code-coverage/connected/*coverage.ec")
    })
}

