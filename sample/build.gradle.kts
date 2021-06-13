plugins {
    kotlin("jvm")
    kotlin("kapt")
}

group = Details.groupId
version = Versions.app

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")
    implementation(project(":annotations"))
    kapt(project(":processor"))
}