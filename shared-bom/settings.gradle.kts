pluginManagement {
    plugins {
        fun prop(name: String): String = extra[name].toString()

        // @formatter:off
        id("com.adarshr.test-logger")            version prop("app.version.gradle-test-logger")
        id("com.netflix.dgs.codegen")            version prop("app.version.graphql-dgs-codegen")
        id("org.jetbrains.kotlin.kapt")          version prop("app.version.kotlin")
        id("org.jetbrains.kotlin.jvm")           version prop("app.version.kotlin")
        id("org.jetbrains.kotlin.plugin.spring") version prop("app.version.kotlin")
        id("org.springframework.boot")           version prop("app.version.spring-boot")
        id("io.spring.dependency-management")    version prop("app.version.spring-dependency-management")
        // @formatter:on
    }
}

rootProject.name = "shared-bom"
