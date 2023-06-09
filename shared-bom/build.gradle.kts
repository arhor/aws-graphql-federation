plugins {
    id("java-platform")
}

dependencies {
    constraints {
        api("org.mapstruct:mapstruct-processor:${property("app.version.mapstruct")}")
        api("com.google.code.findbugs:jsr305:${property("app.version.findbugs-jsr305")}")
        api("org.mapstruct:mapstruct:${property("app.version.mapstruct")}")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${property("app.version.kotlin-coroutines")}")
        api("com.ninja-squad:springmockk:${property("app.version.spring-mockk")}")
    }
}

tasks {
    wrapper {
        gradleVersion = project.property("app.version.gradle").toString()
    }
}
