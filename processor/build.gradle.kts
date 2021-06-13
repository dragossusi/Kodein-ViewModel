plugins {
    kotlin("jvm")
    kotlin("kapt")
    `maven-publish`
}

group = Details.groupId
version = Versions.app

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")
    implementation(project(":annotations"))
    implementation("com.squareup:kotlinpoet:1.7.2")

    //idk what this is for
    implementation("com.google.auto.service:auto-service:1.0")
    kapt("com.google.auto.service:auto-service:1.0")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
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