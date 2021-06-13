// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
        mavenCentral()
    }

    dependencies{
        classpath("com.android.tools.build:gradle:4.1.2")
    }

}

plugins {
//    id("com.android.library") version "4.2.1" apply false
    id("org.jetbrains.kotlin.multiplatform") version Versions.kotlin apply false
//    id("org.jetbrains.kotlin.plugin.serialization") version Versions.kotlin apply false
//    id("org.jetbrains.compose") version Versions.compose apply false
}

allprojects {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

//task clean(type: Delete) {
//    delete rootProject.buildDir
//}
