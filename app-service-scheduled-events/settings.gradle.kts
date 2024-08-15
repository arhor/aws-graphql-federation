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
includeBuild("../lib-starter-testing")
includeBuild("../lib-starter-tracing")

rootProject.name = "app-service-scheduled-events"
