plugins {
    `java-platform`
}

dependencies {
    constraints {
        api("com.google.code.findbugs:jsr305:${libs.versions.findbugs.jsr305.get()}")
        api("com.ninja-squad:springmockk:${libs.versions.spring.mockk.get()}")
        api("io.jsonwebtoken:jjwt-api:${libs.versions.jjwt.get()}")
        api("io.jsonwebtoken:jjwt-impl:${libs.versions.jjwt.get()}")
        api("io.jsonwebtoken:jjwt-jackson:${libs.versions.jjwt.get()}")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${libs.versions.kotlin.coroutines.get()}")
        api("org.mapstruct:mapstruct:${libs.versions.mapstruct.get()}")
        api("org.mapstruct:mapstruct-processor:${libs.versions.mapstruct.get()}")
    }
}

tasks {
    wrapper {
        gradleVersion = libs.versions.gradle.asProvider().get()
    }
}
