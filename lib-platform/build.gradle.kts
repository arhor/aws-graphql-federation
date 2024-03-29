plugins {
    `java-platform`
}

dependencies {
    constraints {
        api("com.google.code.findbugs:jsr305:${libs.versions.findbugs.jsr305.get()}")
        api("com.ninja-squad:springmockk:${libs.versions.spring.mockk.get()}")
        api("com.tngtech.archunit:archunit-junit5:${libs.versions.archunit.get()}")
        api("io.mockk:mockk:${libs.versions.mockk.get()}")
    }
}
