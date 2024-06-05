pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../lib-platform/gradle/libs.versions.toml"))
        }
    }
}

includeBuild("../lib-platform")
includeBuild("../lib-common")
includeBuild("../lib-starter-core")
includeBuild("../lib-starter-graphql")
includeBuild("../lib-spring-starter-security")
includeBuild("../lib-spring-starter-tracing")

rootProject.name = "app-service-posts"
