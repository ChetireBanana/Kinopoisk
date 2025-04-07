// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        mavenCentral()
        google()
    }
    dependencies {
        classpath(libs.navigation.safe.args.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.hiltAndroidGradlePlugin) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.parcelize) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.compose.compiler) apply false

}