// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    //Se agrego este plugin que es el que sugirio la docuemntacion de room
    id("com.google.devtools.ksp") version "1.9.0-1.0.12" apply false

}