plugins {
    `java-platform`
}

dependencies {
    constraints {
        api(libs.archunit.junit5)
        api(libs.awaitility)
        api(libs.findbugs.jsr305)
        api(libs.mockk.core)
        api(libs.mockk.spring)
        api(libs.jetbrains.kotlin.reflect)
        api(libs.jetbrains.kotlin.stdlib.core)
        api(libs.jetbrains.kotlin.stdlib.jdk7)
        api(libs.jetbrains.kotlin.stdlib.jdk8)
    }
}
