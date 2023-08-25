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
        api("io.kotest:kotest-assertions-core-jvm:${libs.versions.kotest.asProvider().get()}")
        api("io.kotest:kotest-framework-datatest-jvm:${libs.versions.kotest.asProvider().get()}")
        api("io.kotest:kotest-runner-junit5-jvm:${libs.versions.kotest.asProvider().get()}")
        api("io.kotest.extensions:kotest-extensions-spring:${libs.versions.kotest.extension.spring.get()}")
        api("io.kotest.extensions:kotest-extensions-testcontainers:${libs.versions.kotest.extension.testcontainers.get()}")
        api("io.mockk:mockk:${libs.versions.mockk.get()}")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${libs.versions.kotlin.coroutines.get()}")

    }
}
