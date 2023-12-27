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
includeBuild("../lib-spring-dgs")
includeBuild("../lib-spring-webmvc-config")
includeBuild("../lib-spring-webmvc-security")
includeBuild("../lib-spring-webmvc-tracing")

rootProject.name = "app-service-posts"
