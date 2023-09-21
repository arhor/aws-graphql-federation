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
        api("io.mockk:mockk:${libs.versions.mockk.get()}")
    }
}
