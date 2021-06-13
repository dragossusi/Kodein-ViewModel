plugins {
    kotlin("multiplatform")
    id("com.android.library")
    `maven-publish`
}


repositories {
    mavenCentral()
}

kotlin {

    android {
        publishLibraryVariants("release", "debug")
    }

    jvm()
    js()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                api("org.kodein.di:kodein-di:${Versions.kodein}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
    }
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        val main by getting {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/androidMain/res")
        }
    }
}

publishing {
    publications {
        publications.withType<MavenPublication> {
            pom {
                name.set("Kodein ViewModel")
                description.set("Generated module for viewModels")
                url.set("http://www.dragossusi.ro/sevens")
            }
        }
    }
}

apply<PublishPlugin>()