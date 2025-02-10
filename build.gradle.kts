// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
//    alias(libs.plugins.gms) apply false
    alias(libs.plugins.compose.compiler) apply true
    alias(libs.plugins.plugin.serialization) apply true
}