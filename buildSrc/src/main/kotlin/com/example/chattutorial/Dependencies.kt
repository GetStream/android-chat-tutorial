package com.example.chattutorial

private const val ANDROID_GRADLE_PLUGIN_VERSION = "4.0.2"
private const val APP_COMPAT_VERSION = "1.2.0"
private const val COIL_VERSION = "1.0.0"
private const val CONSTRAINT_LAYOUT_VERSION = "2.0.1"
private const val KOTLIN_VERSION = "1.4.10"
private const val LIFECYCLE_VIEWMODEL = "2.3.0-beta01"
private const val STREAM_ANDROID_VERSION = "4.3.1-beta-3"

object Dependencies {
    const val androidGradlePlugin = "com.android.tools.build:gradle:$ANDROID_GRADLE_PLUGIN_VERSION"
    const val appCompat = "androidx.appcompat:appcompat:$APP_COMPAT_VERSION"
    const val coil = "io.coil-kt:coil:$COIL_VERSION"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:$CONSTRAINT_LAYOUT_VERSION"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$KOTLIN_VERSION"
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:$KOTLIN_VERSION"
    const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel:$LIFECYCLE_VIEWMODEL"
    const val streamAndroid = "com.github.getstream:stream-chat-android:$STREAM_ANDROID_VERSION"
}
